package com.example.infrastructure.temporal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.infrastructure.temporal.activity.LegacyBridgeActivities;
import com.example.infrastructure.temporal.saga.LegacyTransactionRoutingInput;
import com.example.infrastructure.temporal.saga.LegacyTransactionRoutingResult;
import com.example.infrastructure.temporal.workflow.LegacyTransactionRouteWorkflow;
import com.example.infrastructure.temporal.workflow.LegacyTransactionRouteWorkflowImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * BANK S-33 — integration tests for the CICS/IMS legacy-routing workflow.
 *
 * <p>Two paths exercised end-to-end against {@link TestWorkflowEnvironment}:
 *
 * <ul>
 *   <li>Routing aggregate returns {@code MODERN} → workflow skips the CICS
 *       invocation entirely and only records a checkpoint.</li>
 *   <li>Routing aggregate returns {@code LEGACY} → workflow fires the CICS
 *       activity, then records the checkpoint.</li>
 * </ul>
 */
class LegacyTransactionRouteWorkflowTest {

  private static final String TASK_QUEUE = "legacy-route-test-tq";

  private TestWorkflowEnvironment env;
  private Worker worker;
  private WorkflowClient client;
  private LegacyBridgeActivities activities;

  @BeforeEach
  void setUp() {
    env = TestWorkflowEnvironment.newInstance();
    worker = env.newWorker(TASK_QUEUE);
    worker.registerWorkflowImplementationTypes(LegacyTransactionRouteWorkflowImpl.class);
    activities = mock(LegacyBridgeActivities.class);
    worker.registerActivitiesImplementations(activities);
    env.start();
    client = env.getWorkflowClient();
  }

  @AfterEach
  void tearDown() {
    env.close();
  }

  @Test
  void modernRouteSkipsCicsAndRecordsCheckpoint() {
    when(activities.evaluateRouting(
        anyString(), anyString(), anyString(), anyString(), anyLong(), anyString()))
        .thenReturn("MODERN");
    when(activities.recordSyncCheckpoint(anyString(), anyString(), anyLong(), anyLong()))
        .thenReturn("chkpt-route-1");

    LegacyTransactionRoutingInput input = new LegacyTransactionRoutingInput(
        "route-1", "MODERN", "DEPOSIT", "acct-100", 5_000L, "USD");

    LegacyTransactionRouteWorkflow stub = client.newWorkflowStub(
        LegacyTransactionRouteWorkflow.class,
        WorkflowOptions.newBuilder().setTaskQueue(TASK_QUEUE).setWorkflowId("wf-modern").build());

    LegacyTransactionRoutingResult result = stub.route(input);

    assertTrue(result.success(), "MODERN routing should succeed without CICS");
    assertEquals("MODERN", result.chosenRoute());
    assertNotNull(result.checkpointId());
    verify(activities, never())
        .invokeCicsTransaction(anyString(), anyString(), anyString(), anyLong(), anyString());
    verify(activities).recordSyncCheckpoint(eq("chkpt-route-1"), anyString(), anyLong(), anyLong());
  }

  @Test
  void legacyRouteInvokesCicsBeforeCheckpoint() {
    when(activities.evaluateRouting(
        anyString(), anyString(), anyString(), anyString(), anyLong(), anyString()))
        .thenReturn("LEGACY");
    when(activities.invokeCicsTransaction(
        anyString(), anyString(), anyString(), anyLong(), anyString()))
        .thenReturn("CICS-CONF-route-2");
    when(activities.recordSyncCheckpoint(anyString(), anyString(), anyLong(), anyLong()))
        .thenReturn("chkpt-route-2");

    LegacyTransactionRoutingInput input = new LegacyTransactionRoutingInput(
        "route-2", "CICS", "WITHDRAW", "acct-200", 10_000L, "USD");

    LegacyTransactionRouteWorkflow stub = client.newWorkflowStub(
        LegacyTransactionRouteWorkflow.class,
        WorkflowOptions.newBuilder().setTaskQueue(TASK_QUEUE).setWorkflowId("wf-legacy").build());

    LegacyTransactionRoutingResult result = stub.route(input);

    assertTrue(result.success());
    assertEquals("LEGACY", result.chosenRoute());
    verify(activities)
        .invokeCicsTransaction(eq("route-2"), eq("acct-200"), eq("WITHDRAW"), eq(10_000L), eq("USD"));
    verify(activities).recordSyncCheckpoint(eq("chkpt-route-2"), anyString(), anyLong(), anyLong());
  }
}
