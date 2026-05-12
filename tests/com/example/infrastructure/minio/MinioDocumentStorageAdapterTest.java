package com.example.infrastructure.minio;

import com.example.ports.BucketNotFoundException;
import com.example.ports.DocumentNotFoundException;
import com.example.ports.DocumentStorageException;
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
import io.minio.http.Method;
import io.minio.messages.ErrorResponse;
import okhttp3.Headers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * BANK S-31 — MinIO document-storage adapter integration tests.
 *
 * <p>These tests exercise the {@link MinioDocumentStorageAdapter} through
 * its full code path with a mocked {@link MinioClient}, validating that:
 *
 * <ul>
 *   <li>uploads route content-length-known and content-length-unknown payloads
 *       to the correct {@link PutObjectArgs} stream variant (single-shot vs
 *       multipart);</li>
 *   <li>downloads echo the stored {@code Content-Type} back via the
 *       {@link DocumentStream} wrapper;</li>
 *   <li>delete operations are idempotent (a {@code NoSuchKey} error response
 *       is swallowed, matching S3 semantics);</li>
 *   <li>pre-signed URL generation passes through to
 *       {@link MinioClient#getPresignedObjectUrl} with the right HTTP method
 *       and expiry;</li>
 *   <li>lifecycle policies are translated 1:1 into a MinIO
 *       {@code LifecycleConfiguration};</li>
 *   <li>{@code NoSuchBucket} / {@code NoSuchKey} S3 errors and raw
 *       network failures (ConnectException) are wrapped in port-level
 *       {@link DocumentStorageException} subclasses so application code
 *       never sees a {@code MinioException} import.</li>
 * </ul>
 */
class MinioDocumentStorageAdapterTest {

  private final MinioClient client = mock(MinioClient.class);
  private final MinioProperties props = new MinioProperties();
  private final MinioDocumentStorageAdapter adapter = new MinioDocumentStorageAdapter(client, props);

  // ---------------------------------------------------------------------------
  // upload
  // ---------------------------------------------------------------------------

  @Test
  void uploadWithKnownSizeUsesSingleShotStream() throws Exception {
    byte[] payload = "hello world".getBytes(StandardCharsets.UTF_8);

    adapter.upload("docs", "stmt-1.pdf",
        new ByteArrayInputStream(payload), payload.length, "application/pdf");

    ArgumentCaptor<PutObjectArgs> captor = ArgumentCaptor.forClass(PutObjectArgs.class);
    verify(client).putObject(captor.capture());
    PutObjectArgs args = captor.getValue();
    assertEquals("docs", args.bucket());
    assertEquals("stmt-1.pdf", args.object());
    assertEquals("application/pdf", args.contentType());
    // Known-size mode: objectSize is the real payload length (SDK auto-fills
    // partSize from objectSize when it isn't supplied).
    assertEquals(payload.length, args.objectSize());
  }

  @Test
  void uploadWithUnknownSizeUsesMultipartFromProperties() throws Exception {
    props.setPartSize(8L * 1024 * 1024);

    adapter.upload("docs", "stream.bin",
        new ByteArrayInputStream(new byte[]{1, 2, 3}), -1, "application/octet-stream");

    ArgumentCaptor<PutObjectArgs> captor = ArgumentCaptor.forClass(PutObjectArgs.class);
    verify(client).putObject(captor.capture());
    PutObjectArgs args = captor.getValue();
    assertEquals(-1L, args.objectSize());
    assertEquals(8L * 1024 * 1024, args.partSize());
  }

  @Test
  void uploadDefaultsContentTypeWhenNull() throws Exception {
    adapter.upload("docs", "no-mime", new ByteArrayInputStream(new byte[]{0}), 1, null);

    ArgumentCaptor<PutObjectArgs> captor = ArgumentCaptor.forClass(PutObjectArgs.class);
    verify(client).putObject(captor.capture());
    assertEquals("application/octet-stream", captor.getValue().contentType());
  }

  @Test
  void uploadTranslatesNoSuchBucketToBucketNotFound() throws Exception {
    when(client.putObject(any(PutObjectArgs.class)))
        .thenThrow(errorResponse("NoSuchBucket", "missing", "docs", "stmt-1.pdf"));

    BucketNotFoundException ex = assertThrows(
        BucketNotFoundException.class,
        () -> adapter.upload("docs", "stmt-1.pdf",
            new ByteArrayInputStream(new byte[0]), 0, "application/pdf"));
    assertEquals("docs", ex.getBucket());
  }

  @Test
  void uploadTranslatesIoExceptionToDocumentStorageException() throws Exception {
    when(client.putObject(any(PutObjectArgs.class)))
        .thenThrow(new IOException("connection reset"));

    DocumentStorageException ex = assertThrows(
        DocumentStorageException.class,
        () -> adapter.upload("docs", "stmt-1.pdf",
            new ByteArrayInputStream(new byte[0]), 0, "application/pdf"));
    assertTrue(ex.getMessage().contains("stmt-1.pdf"));
  }

  // ---------------------------------------------------------------------------
  // download
  // ---------------------------------------------------------------------------

  @Test
  void downloadReturnsStreamAndContentTypeFromResponseHeaders() throws Exception {
    byte[] body = "PDF-1.7 ...".getBytes(StandardCharsets.UTF_8);
    Headers headers = new Headers.Builder()
        .add("Content-Type", "application/pdf")
        .add("Content-Length", String.valueOf(body.length))
        .build();
    GetObjectResponse stubbed = new GetObjectResponse(
        headers, "docs", "us-east-1", "stmt-1.pdf", new ByteArrayInputStream(body));
    when(client.getObject(any(GetObjectArgs.class))).thenReturn(stubbed);

    try (DocumentStream stream = adapter.download("docs", "stmt-1.pdf")) {
      assertEquals("application/pdf", stream.contentType());
      assertEquals(body.length, stream.size());
      assertArrayEquals(body, stream.content().readAllBytes());
    }
  }

  @Test
  void downloadTranslatesNoSuchKeyToDocumentNotFound() throws Exception {
    when(client.getObject(any(GetObjectArgs.class)))
        .thenThrow(errorResponse("NoSuchKey", "no such key", "docs", "missing.pdf"));

    DocumentNotFoundException ex = assertThrows(
        DocumentNotFoundException.class,
        () -> adapter.download("docs", "missing.pdf"));
    assertEquals("missing.pdf", ex.getObjectKey());
  }

  @Test
  void downloadTranslatesNoSuchBucketToBucketNotFound() throws Exception {
    when(client.getObject(any(GetObjectArgs.class)))
        .thenThrow(errorResponse("NoSuchBucket", "no bucket", "docs", "any.pdf"));

    assertThrows(BucketNotFoundException.class,
        () -> adapter.download("docs", "any.pdf"));
  }

  // ---------------------------------------------------------------------------
  // delete
  // ---------------------------------------------------------------------------

  @Test
  void deleteIsIdempotentOnNoSuchKey() throws Exception {
    doThrow(errorResponse("NoSuchKey", "no such key", "docs", "gone.pdf"))
        .when(client).removeObject(any(RemoveObjectArgs.class));

    // Must NOT throw — S3 semantics: deleting an absent key is a no-op.
    adapter.delete("docs", "gone.pdf");
    verify(client).removeObject(any(RemoveObjectArgs.class));
  }

  @Test
  void deleteForwardsToMinioClient() throws Exception {
    adapter.delete("docs", "stmt-1.pdf");

    ArgumentCaptor<RemoveObjectArgs> captor = ArgumentCaptor.forClass(RemoveObjectArgs.class);
    verify(client).removeObject(captor.capture());
    assertEquals("docs", captor.getValue().bucket());
    assertEquals("stmt-1.pdf", captor.getValue().object());
  }

  @Test
  void deleteTranslatesNoSuchBucketToBucketNotFound() throws Exception {
    doThrow(errorResponse("NoSuchBucket", "no bucket", "docs", "stmt-1.pdf"))
        .when(client).removeObject(any(RemoveObjectArgs.class));

    assertThrows(BucketNotFoundException.class,
        () -> adapter.delete("docs", "stmt-1.pdf"));
  }

  // ---------------------------------------------------------------------------
  // presigned URLs
  // ---------------------------------------------------------------------------

  @Test
  void presignedGetUrlForwardsExpiryAndReturnsUrl() throws Exception {
    when(client.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
        .thenReturn("https://minio.bank.svc/docs/stmt-1.pdf?signature=abc");

    String url = adapter.presignedGetUrl("docs", "stmt-1.pdf", Duration.ofMinutes(15));

    assertTrue(url.startsWith("https://"));
    ArgumentCaptor<GetPresignedObjectUrlArgs> captor =
        ArgumentCaptor.forClass(GetPresignedObjectUrlArgs.class);
    verify(client).getPresignedObjectUrl(captor.capture());
    assertEquals(Method.GET, captor.getValue().method());
    assertEquals(15 * 60, captor.getValue().expiry());
  }

  @Test
  void presignedGetUrlCapsExpiryAtSevenDays() throws Exception {
    when(client.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
        .thenReturn("https://example/url");

    adapter.presignedGetUrl("docs", "stmt-1.pdf", Duration.ofDays(30));

    ArgumentCaptor<GetPresignedObjectUrlArgs> captor =
        ArgumentCaptor.forClass(GetPresignedObjectUrlArgs.class);
    verify(client).getPresignedObjectUrl(captor.capture());
    // S3/MinIO hard limit is 7 days for pre-signed URLs.
    assertEquals(7 * 24 * 3600, captor.getValue().expiry());
  }

  // ---------------------------------------------------------------------------
  // lifecycle
  // ---------------------------------------------------------------------------

  @Test
  void applyLifecycleRulesBuildsConfigurationFromPortDtos() throws Exception {
    adapter.applyLifecycleRules("docs", List.of(
        LifecycleRule.expireAfterDays("expire-exports", "exports/", 30),
        new LifecycleRule("abort-multipart", "uploads/", null, 7, true)));

    ArgumentCaptor<SetBucketLifecycleArgs> captor =
        ArgumentCaptor.forClass(SetBucketLifecycleArgs.class);
    verify(client).setBucketLifecycle(captor.capture());

    SetBucketLifecycleArgs args = captor.getValue();
    assertEquals("docs", args.bucket());
    List<io.minio.messages.LifecycleRule> rules = args.config().rules();
    assertEquals(2, rules.size());
    assertEquals("expire-exports", rules.get(0).id());
    assertEquals(Integer.valueOf(30), rules.get(0).expiration().days());
    assertEquals(7, rules.get(1).abortIncompleteMultipartUpload().daysAfterInitiation());
  }

  // ---------------------------------------------------------------------------
  // bucketExists
  // ---------------------------------------------------------------------------

  @Test
  void bucketExistsReturnsFromClient() throws Exception {
    when(client.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);
    assertTrue(adapter.bucketExists("docs"));
  }

  @Test
  void bucketExistsTranslatesConnectExceptionToStorageError() throws Exception {
    when(client.bucketExists(any(BucketExistsArgs.class)))
        .thenThrow(new ConnectException("Connection refused"));

    DocumentStorageException ex = assertThrows(
        DocumentStorageException.class,
        () -> adapter.bucketExists("docs"));
    assertTrue(ex.getMessage().contains("unreachable"));
  }

  // ---------------------------------------------------------------------------
  // helpers
  // ---------------------------------------------------------------------------

  private static ErrorResponseException errorResponse(String code, String msg, String bucket, String key) {
    ErrorResponse er = new ErrorResponse(code, msg, bucket, key, "/" + bucket + "/" + key, "req-1", "host-1");
    return new ErrorResponseException(er, null, null);
  }
}
