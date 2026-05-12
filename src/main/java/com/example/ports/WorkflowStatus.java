package com.example.ports;

/**
 * BANK S-33 — provider-neutral workflow execution status.
 *
 * <p>The values mirror the terminal-vs-running distinctions exposed by
 * Temporal's {@code WorkflowExecutionStatus} enum but are decoupled from the
 * generated gRPC enum so {@code com.example.application.*} does not have to
 * import {@code io.temporal.api.enums.v1.WorkflowExecutionStatus}. The
 * adapter performs the mapping.
 *
 * <p>{@link #isTerminal()} is provided so callers can decide whether
 * {@link WorkflowOrchestrationPort#cancelWorkflow(String)} would be a no-op
 * without an extra round-trip.
 */
public enum WorkflowStatus {
  /** Running or continuing-as-new — the workflow is still active. */
  RUNNING,
  /** Workflow returned normally from its @WorkflowMethod. */
  COMPLETED,
  /** Workflow threw an exception that escaped any retry policy. */
  FAILED,
  /** Operator/parent issued a cancel and the workflow honored it. */
  CANCELLED,
  /** Workflow was forcibly terminated (skips compensation). */
  TERMINATED,
  /** Workflow exceeded its execution timeout before completing. */
  TIMED_OUT,
  /** Workflow continued as new — a fresh execution with a new run id is active. */
  CONTINUED_AS_NEW;

  /**
   * True when the workflow has reached a final state and no further events
   * will be appended to its history.
   */
  public boolean isTerminal() {
    return this == COMPLETED
        || this == FAILED
        || this == CANCELLED
        || this == TERMINATED
        || this == TIMED_OUT;
  }
}
