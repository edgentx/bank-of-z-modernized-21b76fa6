package com.example.infrastructure.minio;

import com.example.ports.BucketNotFoundException;
import com.example.ports.DocumentNotFoundException;
import com.example.ports.DocumentStorageException;
import com.example.ports.DocumentStoragePort;
import com.example.ports.DocumentStream;
import com.example.ports.LifecycleRule;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.SetBucketLifecycleArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import io.minio.messages.Expiration;
import io.minio.messages.LifecycleConfiguration;
import io.minio.messages.RuleFilter;
import io.minio.messages.Status;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * BANK S-31 — MinIO-backed {@link DocumentStoragePort} implementation.
 *
 * <p>The adapter is a thin translation layer over the MinIO Java SDK that
 *
 * <ul>
 *   <li>maps every checked {@link MinioException}/{@link IOException} thrown
 *       by the SDK to a port-level {@link DocumentStorageException} (or one
 *       of its more specific subtypes — {@link BucketNotFoundException},
 *       {@link DocumentNotFoundException});</li>
 *   <li>chooses the right {@link PutObjectArgs} stream variant based on
 *       whether content-length is known up front (true multipart streaming
 *       when not);</li>
 *   <li>echoes the stored {@code Content-Type} back from object metadata so
 *       REST controllers can set the response header without re-deriving it
 *       from the filename.</li>
 * </ul>
 */
@Component
public class MinioDocumentStorageAdapter implements DocumentStoragePort {

  private final MinioClient client;
  private final MinioProperties props;

  public MinioDocumentStorageAdapter(MinioClient client, MinioProperties props) {
    this.client = client;
    this.props = props;
  }

  @Override
  public void upload(String bucket, String objectKey, InputStream content, long size, String contentType) {
    try {
      PutObjectArgs.Builder builder = PutObjectArgs.builder()
          .bucket(bucket)
          .object(objectKey)
          .contentType(contentType == null ? "application/octet-stream" : contentType);

      if (size >= 0) {
        // Content-length known — single-shot PUT (S3 still negotiates multipart
        // internally above the SDK's threshold).
        builder.stream(content, size, -1);
      } else {
        // Streaming upload with unknown length — the SDK falls back to
        // multipart, breaking the input into parts of {@code partSize} bytes.
        builder.stream(content, -1, props.getPartSize());
      }

      client.putObject(builder.build());
    } catch (ErrorResponseException e) {
      throw translateErrorResponse(e, bucket, objectKey);
    } catch (MinioException | IOException | InvalidKeyException | NoSuchAlgorithmException e) {
      throw new DocumentStorageException(
          "Failed to upload " + bucket + "/" + objectKey + ": " + e.getMessage(), e);
    }
  }

  @Override
  public DocumentStream download(String bucket, String objectKey) {
    try {
      GetObjectResponse response = client.getObject(
          GetObjectArgs.builder().bucket(bucket).object(objectKey).build());
      String contentType = response.headers().get("Content-Type");
      long size = parseContentLength(response.headers().get("Content-Length"));
      return new DocumentStream(response, contentType, size);
    } catch (ErrorResponseException e) {
      throw translateErrorResponse(e, bucket, objectKey);
    } catch (MinioException | IOException | InvalidKeyException | NoSuchAlgorithmException e) {
      throw new DocumentStorageException(
          "Failed to download " + bucket + "/" + objectKey + ": " + e.getMessage(), e);
    }
  }

  @Override
  public void delete(String bucket, String objectKey) {
    try {
      client.removeObject(
          RemoveObjectArgs.builder().bucket(bucket).object(objectKey).build());
    } catch (ErrorResponseException e) {
      // S3 delete is idempotent — a NoSuchKey response is not an error from
      // the caller's perspective.
      String code = e.errorResponse() == null ? "" : e.errorResponse().code();
      if ("NoSuchBucket".equals(code)) {
        throw new BucketNotFoundException(bucket, e);
      }
      if ("NoSuchKey".equals(code)) {
        return;
      }
      throw new DocumentStorageException(
          "Failed to delete " + bucket + "/" + objectKey + ": " + e.getMessage(), e);
    } catch (MinioException | IOException | InvalidKeyException | NoSuchAlgorithmException e) {
      throw new DocumentStorageException(
          "Failed to delete " + bucket + "/" + objectKey + ": " + e.getMessage(), e);
    }
  }

  @Override
  public String presignedGetUrl(String bucket, String objectKey, Duration expiry) {
    int expirySeconds = (int) Math.min(expiry.getSeconds(), TimeUnit.DAYS.toSeconds(7));
    try {
      return client.getPresignedObjectUrl(
          GetPresignedObjectUrlArgs.builder()
              .method(Method.GET)
              .bucket(bucket)
              .object(objectKey)
              .expiry(expirySeconds)
              .build());
    } catch (ErrorResponseException e) {
      throw translateErrorResponse(e, bucket, objectKey);
    } catch (MinioException | IOException | InvalidKeyException | NoSuchAlgorithmException e) {
      throw new DocumentStorageException(
          "Failed to presign " + bucket + "/" + objectKey + ": " + e.getMessage(), e);
    }
  }

  @Override
  public void applyLifecycleRules(String bucket, List<LifecycleRule> rules) {
    List<io.minio.messages.LifecycleRule> minioRules = new ArrayList<>(rules.size());
    for (LifecycleRule r : rules) {
      Expiration expiration = null;
      if (r.getExpiryDays() != null) {
        expiration = new Expiration((ZonedDateTime) null, r.getExpiryDays(), null);
      }
      io.minio.messages.AbortIncompleteMultipartUpload abort = null;
      if (r.getAbortIncompleteMultipartUploadDays() != null) {
        abort = new io.minio.messages.AbortIncompleteMultipartUpload(
            r.getAbortIncompleteMultipartUploadDays());
      }
      minioRules.add(new io.minio.messages.LifecycleRule(
          r.isEnabled() ? Status.ENABLED : Status.DISABLED,
          abort,
          expiration,
          new RuleFilter(r.getPrefix()),
          r.getId(),
          null,
          null,
          null));
    }
    try {
      client.setBucketLifecycle(
          SetBucketLifecycleArgs.builder()
              .bucket(bucket)
              .config(new LifecycleConfiguration(minioRules))
              .build());
    } catch (ErrorResponseException e) {
      throw translateErrorResponse(e, bucket, null);
    } catch (MinioException | IOException | InvalidKeyException | NoSuchAlgorithmException e) {
      throw new DocumentStorageException(
          "Failed to apply lifecycle on " + bucket + ": " + e.getMessage(), e);
    }
  }

  @Override
  public boolean bucketExists(String bucket) {
    try {
      return client.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
    } catch (ConnectException | UnknownHostException e) {
      throw new DocumentStorageException("MinIO unreachable: " + e.getMessage(), e);
    } catch (MinioException | IOException | InvalidKeyException | NoSuchAlgorithmException e) {
      throw new DocumentStorageException(
          "Failed to check bucket " + bucket + ": " + e.getMessage(), e);
    }
  }

  private static DocumentStorageException translateErrorResponse(
      ErrorResponseException e, String bucket, String objectKey) {
    String code = e.errorResponse() == null ? "" : e.errorResponse().code();
    if ("NoSuchBucket".equals(code)) {
      return new BucketNotFoundException(bucket, e);
    }
    if (("NoSuchKey".equals(code) || "NoSuchObject".equals(code)) && objectKey != null) {
      return new DocumentNotFoundException(bucket, objectKey, e);
    }
    return new DocumentStorageException(
        "MinIO error " + code + " on " + bucket
            + (objectKey == null ? "" : "/" + objectKey)
            + ": " + e.getMessage(), e);
  }

  private static long parseContentLength(String header) {
    if (header == null) {
      return -1;
    }
    try {
      return Long.parseLong(header);
    } catch (NumberFormatException ignored) {
      return -1;
    }
  }
}
