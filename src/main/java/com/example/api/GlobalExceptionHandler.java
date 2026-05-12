package com.example.api;

import com.example.application.AggregateNotFoundException;
import com.example.domain.shared.UnknownCommandException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * Maps the codebase's domain + application exception taxonomy to HTTP
 * responses. Order of handlers matters for subclassing: more specific
 * exceptions are declared with their own method so they win over the
 * generic {@code Exception} fallback.
 *
 *   AggregateNotFoundException     → 404
 *   IllegalArgumentException       → 400 (bad input + record-canonical-ctor failures)
 *   IllegalStateException          → 400 (aggregate invariant violations)
 *   MethodArgumentNotValidException → 400 (Bean Validation on @RequestBody)
 *   UnknownCommandException        → 500 (programmer error — should never reach API)
 *   Throwable                      → 500
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(AggregateNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(AggregateNotFoundException ex) {
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(new ErrorResponse(404, "Not Found", ex.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleBeanValidation(MethodArgumentNotValidException ex) {
    List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(fe -> new ErrorResponse.FieldError(fe.getField(), fe.getDefaultMessage()))
        .toList();
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(400, "Bad Request", "Request validation failed", fieldErrors));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleUnreadableBody(HttpMessageNotReadableException ex) {
    // Common cause: record canonical-constructor IAE during Jackson deserialization
    // (e.g. EndSessionCmd rejects blank sessionId) — surface as 400 not 500.
    String message = ex.getMostSpecificCause() != null
        ? ex.getMostSpecificCause().getMessage()
        : "Malformed request body";
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(400, "Bad Request", message));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(400, "Bad Request", ex.getMessage()));
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ErrorResponse> handleInvariantViolation(IllegalStateException ex) {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(400, "Bad Request", ex.getMessage()));
  }

  @ExceptionHandler(UnknownCommandException.class)
  public ResponseEntity<ErrorResponse> handleUnknownCommand(UnknownCommandException ex) {
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorResponse(500, "Internal Server Error", ex.getMessage()));
  }

  @ExceptionHandler(Throwable.class)
  public ResponseEntity<ErrorResponse> handleUnexpected(Throwable ex) {
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorResponse(500, "Internal Server Error", ex.getMessage()));
  }
}
