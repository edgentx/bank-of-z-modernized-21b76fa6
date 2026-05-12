package com.example.infrastructure.temporal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.ports.WorkflowStatus;
import io.temporal.api.enums.v1.WorkflowExecutionStatus;
import org.junit.jupiter.api.Test;

/**
 * BANK S-33 — pin the {@code Temporal gRPC enum} → {@link WorkflowStatus}
 * mapping so a regression in
 * {@link TemporalWorkflowOrchestrationAdapter#mapStatus} can't quietly
 * misreport workflow state to callers.
 *
 * <p>The mapping is the only place the application layer's status enum
 * meets Temporal's, so getting it wrong manifests as a workflow that looks
 * RUNNING in dashboards but is actually FAILED (or vice-versa). Worth a
 * dedicated table-style test.
 */
class WorkflowStatusMappingTest {

  @Test
  void completedMapsToCompleted() {
    assertEquals(WorkflowStatus.COMPLETED,
        TemporalWorkflowOrchestrationAdapter.mapStatus(WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_COMPLETED));
  }

  @Test
  void failedMapsToFailed() {
    assertEquals(WorkflowStatus.FAILED,
        TemporalWorkflowOrchestrationAdapter.mapStatus(WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_FAILED));
  }

  @Test
  void canceledMapsToCancelled() {
    assertEquals(WorkflowStatus.CANCELLED,
        TemporalWorkflowOrchestrationAdapter.mapStatus(WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_CANCELED));
  }

  @Test
  void terminatedMapsToTerminated() {
    assertEquals(WorkflowStatus.TERMINATED,
        TemporalWorkflowOrchestrationAdapter.mapStatus(WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_TERMINATED));
  }

  @Test
  void timedOutMapsToTimedOut() {
    assertEquals(WorkflowStatus.TIMED_OUT,
        TemporalWorkflowOrchestrationAdapter.mapStatus(WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_TIMED_OUT));
  }

  @Test
  void continuedAsNewMapsToContinuedAsNew() {
    assertEquals(WorkflowStatus.CONTINUED_AS_NEW,
        TemporalWorkflowOrchestrationAdapter.mapStatus(WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_CONTINUED_AS_NEW));
  }

  @Test
  void runningMapsToRunning() {
    assertEquals(WorkflowStatus.RUNNING,
        TemporalWorkflowOrchestrationAdapter.mapStatus(WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_RUNNING));
  }

  @Test
  void unspecifiedMapsToRunningConservatively() {
    assertEquals(WorkflowStatus.RUNNING,
        TemporalWorkflowOrchestrationAdapter.mapStatus(WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_UNSPECIFIED),
        "unrecorded status should be treated as still-running, never silently terminal");
  }

  @Test
  void isTerminalReflectsFinalStates() {
    assertTrue(WorkflowStatus.COMPLETED.isTerminal());
    assertTrue(WorkflowStatus.FAILED.isTerminal());
    assertTrue(WorkflowStatus.CANCELLED.isTerminal());
    assertTrue(WorkflowStatus.TERMINATED.isTerminal());
    assertTrue(WorkflowStatus.TIMED_OUT.isTerminal());
    assertFalse(WorkflowStatus.RUNNING.isTerminal());
    assertFalse(WorkflowStatus.CONTINUED_AS_NEW.isTerminal(),
        "continue-as-new yields a fresh execution; the original is closed but the chain is still active");
  }
}
