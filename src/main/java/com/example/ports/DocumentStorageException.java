package com.example.ports;

/**
 * Domain-friendly wrapper for blob-store failures so application services do
 * not have to depend on the MinIO/AWS SDK exception hierarchy.
 */
public class DocumentStorageException extends RuntimeException {

  public DocumentStorageException(String message) {
    super(message);
  }

  public DocumentStorageException(String message, Throwable cause) {
    super(message, cause);
  }
}
