package com.example.ports;

/** Thrown by {@link DocumentStoragePort} when an operation targets a missing bucket. */
public class BucketNotFoundException extends DocumentStorageException {

  private final String bucket;

  public BucketNotFoundException(String bucket) {
    super("Bucket not found: " + bucket);
    this.bucket = bucket;
  }

  public BucketNotFoundException(String bucket, Throwable cause) {
    super("Bucket not found: " + bucket, cause);
    this.bucket = bucket;
  }

  public String getBucket() {
    return bucket;
  }
}
