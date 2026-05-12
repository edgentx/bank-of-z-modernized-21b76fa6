package com.example.ports;

/** Thrown by {@link DocumentStoragePort#download} when the object key is absent. */
public class DocumentNotFoundException extends DocumentStorageException {

  private final String bucket;
  private final String objectKey;

  public DocumentNotFoundException(String bucket, String objectKey) {
    super("Document not found: " + bucket + "/" + objectKey);
    this.bucket = bucket;
    this.objectKey = objectKey;
  }

  public DocumentNotFoundException(String bucket, String objectKey, Throwable cause) {
    super("Document not found: " + bucket + "/" + objectKey, cause);
    this.bucket = bucket;
    this.objectKey = objectKey;
  }

  public String getBucket() {
    return bucket;
  }

  public String getObjectKey() {
    return objectKey;
  }
}
