package com.example.infrastructure.temporal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.infrastructure.temporal.activity.AccountOpeningActivities;
import com.example.infrastructure.temporal.activity.LegacyBridgeActivities;
import com.example.infrastructure.temporal.saga.AccountOpeningInput;
import com.example.infrastructure.temporal.saga.LegacyTransactionRoutingInput;
import com.example.infrastructure.temporal.workflow.AccountOpeningWorkflowImpl;
import com.example.infrastructure.temporal.workflow.LegacyTransactionRouteWorkflowImpl;
import com.example.ports.WorkflowException;
import com.example.ports.WorkflowExecutionHandle;
import com.example.ports.WorkflowStatus;
import io.temporal.client.WorkflowClient;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * BANK S-33 — adapter-surface tests for
 * {@link TemporalWorkflowOrchestrationAdapter}.
 *
 * <p>Hits the three public methods of the {@link com.example.ports.WorkflowOrchestrationPort}
 * contract — {@code startWorkflow}, {@code queryStatus}, {@code cancelWorkflow}
 * — through an in-process {@link TestWorkflowEnvironment} so the gRPC plumbing,
 * the workflow-type dispatch, and the status-translation are all covered
 * without an external Temporal server.
 */
class TemporalWorkflowOrchestrationAdapterTest {

  private static final String TASK_QUEUE = "adapter-test-tq";

  private TestWorkflowEnvironment env;
  private WorkflowClient client;
  private TemporalWorkflowOrchestrationAdapter adapter;
  private TemporalProperties props;
  private AccountOpeningActivities accountActivities;
  private LegacyBridgeActivities legacyActivities;

  @BeforeEach
  void setUp() {
    env = TestWorkflowEnvironment.newInstance();
    Worker worker = env.newWorker(TASK_QUEUE);
    worker.registerWorkflowImplementationTypes(
        AccountOpeningWorkflowImpl.class, LegacyTransactionRouteWorkflowImpl.class);

    accountActivities = mock(AccountOpeningActivities.class);
    legacyActivities = mock(LegacyBridgeActivities.class);
    // Stub the routing activity so the legacy workflow has a path to
    // completion — otherwise queryStatus would race against worker startup.
    when(legacyActivities.evaluateRouting(
        anyString(), anyString(), anyString(), anyString(), anyLong(), anyString()))
        .thenReturn("MODERN");
    when(legacyActivities.recordSyncCheckpoint(anyString(), anyString(), anyLong(), anyLong()))
        .thenReturn("chkpt-adapter");

    worker.registerActivitiesImplementations(accountActivities, legacyActivities);
    env.start();
    client = env.getWorkflowClient();

    props = new TemporalProperties();
    props.setNamespace(client.getOptions().getNamespace());
    props.setTaskQueue(TASK_QUEUE);
    adapter = new TemporalWorkflowOrchestrationAdapter(client, props);
  }

  @AfterEach
  void tearDown() {
    env.close();
  }

  // ---------------------------------------------------------------------------
  // startWorkflow
  // ---------------------------------------------------------------------------

  @Test
  void startWorkflowReturnsHandleForAccountOpening() {
    AccountOpeningInput input = new AccountOpeningInput(
        "cust-a", "Margaret", "Hamilton", "mh@example.com",
        "acct-a", "CHECKING", "60-83-71", 0L, "USD");

    WorkflowExecutionHandle handle =
        adapter.startWorkflow("AccountOpeningWorkflow", "wf-start-1", input);

    assertEquals("wf-start-1", handle.workflowId());
    assertNotNull(handle.runId());
    assertFalse(handle.runId().isBlank());
    assertEquals(WorkflowStatus.RUNNING, handle.status());
  }

  @Test
  void startWorkflowRoutesLegacyType() {
    LegacyTransactionRoutingInput input = new LegacyTransactionRoutingInput(
        "route-a", "MODERN", "DEPOSIT", "acct-a", 1_00L, "USD");

    WorkflowExecutionHandle handle =
        adapter.startWorkflow("LegacyTransactionRouteWorkflow", "wf-route-1", input);

    assertEquals("wf-route-1", handle.workflowId());
    assertEquals(WorkflowStatus.RUNNING, handle.status());
  }

  @Test
  void startWorkflowRejectsUnknownType() {
    WorkflowException ex = assertThrows(WorkflowException.class,
        () -> adapter.startWorkflow("DoesNotExist", "wf-nope", "payload"));
    assertTrue(ex.getMessage().contains("DoesNotExist"));
  }

  // ---------------------------------------------------------------------------
  // queryStatus
  // ---------------------------------------------------------------------------

  @Test
  void queryStatusReturnsEmptyForMissingWorkflow() {
    Optional<WorkflowStatus> status = adapter.queryStatus("no-such-workflow-id");
    assertTrue(status.isEmpty(),
        "queryStatus on a missing workflow must return empty, not throw");
  }

  // ---------------------------------------------------------------------------
  // cancelWorkflow
  // ---------------------------------------------------------------------------

  @Test
  void cancelWorkflowOnMissingWorkflowSurfacesAsWorkflowException() {
    // Temporal's gRPC layer rejects cancel on a non-existent workflow id;
    // the adapter must wrap that as a port-level WorkflowException so
    // application callers don't import io.grpc.*.
    assertThrows(WorkflowException.class, () -> adapter.cancelWorkflow("no-such-workflow-id"));
  }
}
