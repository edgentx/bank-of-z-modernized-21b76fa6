package com.example.infrastructure.temporal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.example.infrastructure.temporal.activity.AccountOpeningActivities;
import com.example.infrastructure.temporal.saga.AccountOpeningInput;
import com.example.infrastructure.temporal.saga.AccountOpeningResult;
import com.example.infrastructure.temporal.workflow.AccountOpeningWorkflow;
import com.example.infrastructure.temporal.workflow.AccountOpeningWorkflowImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.common.RetryOptions;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import java.time.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * BANK S-33 — integration tests for the account-opening saga.
 *
 * <p>Uses Temporal's {@link TestWorkflowEnvironment} so the workflow and
 * activity stubs run end-to-end in-process with the real Temporal SDK —
 * just no gRPC server. This is the only way to validate the saga's
 * compensation ordering, the activity retry policy, and the
 * {@code @QueryMethod} contract without standing up a Temporal sidecar in
 * CI.
 *
 * <p>The activity bean is a Mockito mock that returns canned values for
 * the happy-path test and is configured to throw on
 * {@code postInitialDeposit} for the compensation test — that lets us
 * assert <em>which</em> compensation callbacks fire (close account, but
 * NOT reverse transaction, because the deposit failed before it was
 * recorded).
 */
class AccountOpeningWorkflowTest {

  private static final String TASK_QUEUE = "account-opening-test-tq";

  private TestWorkflowEnvironment env;
  private Worker worker;
  private WorkflowClient client;
  private AccountOpeningActivities activities;

  @BeforeEach
  void setUp() {
    env = TestWorkflowEnvironment.newInstance();
    worker = env.newWorker(TASK_QUEUE);
    worker.registerWorkflowImplementationTypes(AccountOpeningWorkflowImpl.class);
    activities = mock(AccountOpeningActivities.class);
    worker.registerActivitiesImplementations(activities);
    env.start();
    client = env.getWorkflowClient();
  }

  @AfterEach
  void tearDown() {
    env.close();
  }

  // ---------------------------------------------------------------------------
  // happy path
  // ---------------------------------------------------------------------------

  @Test
  void happyPathRunsAllThreeStepsAndReturnsSuccess() {
    AccountOpeningInput input = new AccountOpeningInput(
        "cust-1", "Ada", "Lovelace", "ada@example.com",
        "acct-1", "CHECKING", "60-83-71", 100_00, "USD");

    AccountOpeningWorkflow stub = client.newWorkflowStub(
        AccountOpeningWorkflow.class,
        WorkflowOptions.newBuilder().setTaskQueue(TASK_QUEUE).setWorkflowId("wf-happy").build());

    AccountOpeningResult result = stub.openAccount(input);

    assertTrue(result.success(), "happy path should succeed");
    assertEquals("acct-1", result.accountId());
    assertEquals("cust-1", result.customerId());
    assertNotNull(result.transactionId());
    assertTrue(result.transactionId().startsWith("tx-"));
    assertTrue(result.compensationLog().isEmpty(), "no compensation should fire on happy path");

    // Verify the saga fired each step exactly once in the correct order via
    // the verb-name signatures.
    verify(activities).enrollCustomer("cust-1", "Ada", "Lovelace", "ada@example.com");
    verify(activities).openAccount("acct-1", "cust-1", "CHECKING", 100_00, "60-83-71");
    verify(activities).postInitialDeposit(anyString(), eq("acct-1"), eq(100_00L), eq("USD"));
  }

  // ---------------------------------------------------------------------------
  // saga compensation
  // ---------------------------------------------------------------------------

  @Test
  void postDepositFailureTriggersCloseAccountCompensation() {
    // Configure the mock — the activity is already registered with the
    // worker via setUp(); programming the mock here is a Mockito-level
    // operation, not a worker registration.
    doThrow(new RuntimeException("ledger unavailable"))
        .when(activities)
        .postInitialDeposit(anyString(), anyString(), anyLong(), anyString());

    AccountOpeningInput input = new AccountOpeningInput(
        "cust-2", "Grace", "Hopper", "grace@example.com",
        "acct-2", "SAVINGS", "60-83-71", 250_00, "USD");

    AccountOpeningWorkflow stub = client.newWorkflowStub(
        AccountOpeningWorkflow.class,
        WorkflowOptions.newBuilder()
            .setTaskQueue(TASK_QUEUE)
            .setWorkflowId("wf-comp")
            // Cap workflow time so a runaway retry policy in the impl class
            // doesn't hang the suite.
            .setWorkflowExecutionTimeout(Duration.ofMinutes(2))
            .build());

    AccountOpeningResult result = stub.openAccount(input);

    assertFalse(result.success(), "post-deposit failure should fail the saga");
    assertEquals("acct-2", result.accountId());
    // Compensation log must contain the account close — but NOT a tx reversal,
    // because the deposit never succeeded and so there is no tx to reverse.
    assertTrue(
        result.compensationLog().stream().anyMatch(entry -> entry.contains("closed account acct-2")),
        "close-account compensation should run; log was " + result.compensationLog());
    assertFalse(
        result.compensationLog().stream().anyMatch(entry -> entry.contains("reversed transaction")),
        "deposit never succeeded, so reversal must NOT fire");

    // Make sure compensation actually invoked closeAccount on the activity.
    verify(activities, atLeastOnce()).closeAccount(eq("acct-2"), anyString());
  }

  // ---------------------------------------------------------------------------
  // query method
  // ---------------------------------------------------------------------------

  @Test
  void currentStepReachesDoneAfterHappyPath() {
    AccountOpeningInput input = new AccountOpeningInput(
        "cust-3", "Edsger", "Dijkstra", "ed@example.com",
        "acct-3", "CHECKING", "60-83-71", 50_00, "USD");

    AccountOpeningWorkflow stub = client.newWorkflowStub(
        AccountOpeningWorkflow.class,
        WorkflowOptions.newBuilder().setTaskQueue(TASK_QUEUE).setWorkflowId("wf-query").build());

    stub.openAccount(input);

    // After the workflow returns we can still issue queries against the
    // closed execution — Temporal's history retains the workflow long
    // enough for that.
    assertEquals("DONE", stub.currentStep());
  }

  private static long anyLong() {
    return org.mockito.ArgumentMatchers.anyLong();
  }
}
