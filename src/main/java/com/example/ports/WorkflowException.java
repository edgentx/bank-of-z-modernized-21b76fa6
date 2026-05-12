package com.example.ports;

/**
 * BANK S-33 — port-level exception for {@link WorkflowOrchestrationPort}.
 *
 * <p>Wraps adapter-specific failures (Temporal {@code StatusRuntimeException},
 * {@code WorkflowServiceException}, etc.) so application code never imports
 * {@code io.temporal.*} or {@code io.grpc.*} just to handle a workflow
 * transport error — mirrors the {@link CacheException}/{@link DocumentStorageException}
 * pattern from earlier infrastructure stories.
 */
public class WorkflowException extends RuntimeException {
  public WorkflowException(String message) {
    super(message);
  }

  public WorkflowException(String message, Throwable cause) {
    super(message, cause);
  }
}
