package com.example.api;

import com.example.application.AggregateNotFoundException;
import com.example.domain.shared.UnknownCommandException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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

  /**
   * Missing route — Spring throws {@link NoResourceFoundException} when no
   * controller method matches the request. Map to 404 so callers can
   * distinguish "endpoint not implemented yet" from "endpoint blew up at
   * runtime" — without this the catch-all Throwable handler swallows the
   * exception and returns 500, which destroys ops signal during deploys
   * and trips uptime monitors expecting 5xx to mean a real incident. (#81)
   */
  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ErrorResponse> handleNoResource(NoResourceFoundException ex) {
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(new ErrorResponse(404, "Not Found", "No handler for " + ex.getResourcePath()));
  }

  /**
   * Explicit {@link ResponseStatusException} thrown from handlers — respect
   * the embedded status code. Without this, the catch-all below would
   * remap to 500 regardless of the intended status (a controller throwing
   * {@code new ResponseStatusException(NOT_FOUND, "screen X")} would
   * surface as 500 to the client).
   */
  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ErrorResponse> handleResponseStatus(ResponseStatusException ex) {
    HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
    return ResponseEntity
        .status(status)
        .body(new ErrorResponse(status.value(), status.getReasonPhrase(), ex.getReason()));
  }

  @ExceptionHandler(Throwable.class)
  public ResponseEntity<ErrorResponse> handleUnexpected(Throwable ex) {
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorResponse(500, "Internal Server Error", ex.getMessage()));
  }
}
