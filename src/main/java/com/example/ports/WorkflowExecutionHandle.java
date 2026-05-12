package com.example.ports;

/**
 * BANK S-33 — handle returned from
 * {@link WorkflowOrchestrationPort#startWorkflow(String, String, Object)}.
 *
 * <p>{@code workflowId} is the caller-supplied business identifier (stable
 * across retries / continue-as-new), {@code runId} is the Temporal-assigned
 * per-execution identifier — useful for log correlation but transient: a
 * continue-as-new will mint a new run id under the same workflow id.
 *
 * <p>{@code status} is the status snapshot taken at start time (usually
 * {@link WorkflowStatus#RUNNING}); callers must use
 * {@link WorkflowOrchestrationPort#queryStatus(String)} for a fresh read.
 */
public record WorkflowExecutionHandle(String workflowId, String runId, WorkflowStatus status) {
}
