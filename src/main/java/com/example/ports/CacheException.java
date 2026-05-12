package com.example.ports;

/**
 * Domain-friendly wrapper for cache failures so application services do not
 * have to depend on Spring Data Redis / Lettuce / Jackson exception
 * hierarchies. Mirrors {@link DocumentStorageException} from S-31.
 */
public class CacheException extends RuntimeException {

  public CacheException(String message) {
    super(message);
  }

  public CacheException(String message, Throwable cause) {
    super(message, cause);
  }
}
