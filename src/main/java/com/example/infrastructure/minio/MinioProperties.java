package com.example.infrastructure.minio;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Externalized config for the MinIO document-storage adapter (S-31).
 *
 * <p>Bound to {@code storage.minio.*} keys in {@code application.properties}
 * (and overridable per environment via env vars
 * {@code STORAGE_MINIO_ENDPOINT}, {@code STORAGE_MINIO_ACCESS_KEY},
 * {@code STORAGE_MINIO_SECRET_KEY}, etc.). Kept as plain getter/setter POJO
 * so Spring's {@code @ConfigurationProperties} binder can populate it
 * without ceremony.
 */
@ConfigurationProperties(prefix = "storage.minio")
public class MinioProperties {

  /** Full MinIO/S3 endpoint URL, e.g. {@code https://minio.bank.svc:9000}. */
  private String endpoint = "http://localhost:9000";

  /** S3 access key id. */
  private String accessKey = "minioadmin";

  /** S3 secret access key. */
  private String secretKey = "minioadmin";

  /** S3 region; MinIO defaults to {@code us-east-1} regardless of deployment. */
  private String region = "us-east-1";

  /** Default bucket new documents land in if no other bucket is specified. */
  private String defaultBucket = "bank-documents";

  /**
   * Multipart-upload part size in bytes (min 5 MiB, max 5 GiB per S3 spec).
   * The SDK uses this when the upload's content-length is unknown.
   */
  private long partSize = 10L * 1024L * 1024L; // 10 MiB

  /** TLS-cert validation toggle for self-signed MinIO clusters used in dev. */
  private boolean secure = false;

  public String getEndpoint() {
    return endpoint;
  }

  public void setEndpoint(String endpoint) {
    this.endpoint = endpoint;
  }

  public String getAccessKey() {
    return accessKey;
  }

  public void setAccessKey(String accessKey) {
    this.accessKey = accessKey;
  }

  public String getSecretKey() {
    return secretKey;
  }

  public void setSecretKey(String secretKey) {
    this.secretKey = secretKey;
  }

  public String getRegion() {
    return region;
  }

  public void setRegion(String region) {
    this.region = region;
  }

  public String getDefaultBucket() {
    return defaultBucket;
  }

  public void setDefaultBucket(String defaultBucket) {
    this.defaultBucket = defaultBucket;
  }

  public long getPartSize() {
    return partSize;
  }

  public void setPartSize(long partSize) {
    this.partSize = partSize;
  }

  public boolean isSecure() {
    return secure;
  }

  public void setSecure(boolean secure) {
    this.secure = secure;
  }
}
