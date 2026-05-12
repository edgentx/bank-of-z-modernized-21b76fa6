package com.example.api;

import java.time.Instant;
import java.util.List;

/**
 * Uniform error payload returned by the global exception handler.
 * Renders as JSON via Jackson's record-component-based serialization.
 */
public record ErrorResponse(
    int status,
    String error,
    String message,
    List<FieldError> fieldErrors,
    Instant timestamp
) {
  public ErrorResponse(int status, String error, String message) {
    this(status, error, message, List.of(), Instant.now());
  }

  public ErrorResponse(int status, String error, String message, List<FieldError> fieldErrors) {
    this(status, error, message, fieldErrors, Instant.now());
  }

  public record FieldError(String field, String message) {}
}
