package com.example.infrastructure.temporal.workflow;

import com.example.infrastructure.temporal.activity.AccountOpeningActivities;
import com.example.infrastructure.temporal.saga.AccountOpeningInput;
import com.example.infrastructure.temporal.saga.AccountOpeningResult;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.failure.ActivityFailure;
import io.temporal.workflow.Saga;
import io.temporal.workflow.Workflow;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * BANK S-33 — Temporal workflow implementation for the account-opening
 * saga.
 *
 * <p>Implements the saga with Temporal's built-in {@link Saga} class: each
 * forward activity registers its compensation up-front so when a downstream
 * step throws, the saga walks the registered compensations in reverse and
 * runs them under the same retry policy. This is the textbook
 * orchestration-saga pattern (vs choreography) — chosen here because the
 * account-opening sequence has a small, well-defined number of steps and a
 * central coordinator makes the failure surface easy to reason about.
 *
 * <p><strong>Determinism</strong>: Temporal replays the workflow by
 * re-executing this code from event history, so anything non-deterministic
 * (current time, random ids, Spring bean lookups, system properties) must
 * go through the Temporal SDK. We use {@link Workflow#randomUUID()} for
 * the transaction id rather than {@code UUID.randomUUID()} — the latter
 * would return a fresh value on every replay and break history matching.
 *
 * <p><strong>Activity stubs</strong> are minted via
 * {@link Workflow#newActivityStub} with options built from the workflow's
 * input — this keeps configuration in one place and lets tests inject a
 * mock {@link AccountOpeningActivities} into the worker without touching
 * the workflow code.
 */
public class AccountOpeningWorkflowImpl implements AccountOpeningWorkflow {

  /**
   * Mutable label tracked across steps so the {@code @QueryMethod}
   * {@link #currentStep()} can return a meaningful snapshot at any point.
   * Temporal guarantees workflow code runs single-threaded against its
   * event loop, so plain field mutation here is safe (no synchronization
   * needed; replay rebuilds this state from history).
   */
  private volatile String currentStep = "INITIALIZED";

  @Override
  public AccountOpeningResult openAccount(AccountOpeningInput input) {
    AccountOpeningActivities activities = buildActivityStub();

    Saga saga = new Saga(new Saga.Options.Builder()
        .setParallelCompensation(false) // sequential compensation: reverse-then-close, never simultaneous
        .build());
    List<String> compensationLog = new ArrayList<>();

    String transactionId = "tx-" + Workflow.randomUUID();

    try {
      // ---- step 1: enroll customer (idempotent, no compensation needed) ----
      currentStep = "ENROLLING_CUSTOMER";
      activities.enrollCustomer(input.customerId(), input.firstName(), input.lastName(), input.email());

      // ---- step 2: open account ----
      // Register compensation AFTER the forward step succeeds so the saga
      // only collects undo actions for steps that actually committed —
      // otherwise a failure inside step 2 itself would try to compensate
      // an account that was never created. Same rule for step 3.
      currentStep = "OPENING_ACCOUNT";
      activities.openAccount(input.accountId(), input.customerId(), input.accountType(),
          input.initialDepositCents(), input.sortCode());
      saga.addCompensation(() -> {
        activities.closeAccount(input.accountId(), "saga compensation: post-deposit failed");
        compensationLog.add("closed account " + input.accountId());
      });

      // ---- step 3: post initial deposit ----
      currentStep = "POSTING_DEPOSIT";
      activities.postInitialDeposit(transactionId, input.accountId(),
          input.initialDepositCents(), input.currency());
      saga.addCompensation(() -> {
        activities.reverseTransaction(transactionId, "saga compensation: post-deposit reversal");
        compensationLog.add("reversed transaction " + transactionId);
      });

      currentStep = "DONE";
      return AccountOpeningResult.succeeded(input.accountId(), input.customerId(), transactionId);
    } catch (ActivityFailure failure) {
      // ---- compensation path ----
      currentStep = "COMPENSATING";
      saga.compensate();
      currentStep = "FAILED";
      // ActivityFailure wraps an ApplicationFailure with the original message;
      // surface the cause message so operators see the domain error (e.g.
      // "account already open") not just "activity failed".
      String reason = failure.getCause() != null && failure.getCause().getMessage() != null
          ? failure.getCause().getMessage()
          : failure.getMessage();
      return AccountOpeningResult.failed(
          input.accountId(), input.customerId(), transactionId, compensationLog, reason);
    }
  }

  @Override
  public String currentStep() {
    return currentStep;
  }

  private AccountOpeningActivities buildActivityStub() {
    // Retry semantics: 5 attempts with exponential backoff. The activity
    // implementation is idempotent (saga steps are designed to be re-played
    // on retry), so a transient gRPC blip during step 2 will be invisible
    // to the caller — only persistent failures escape as ActivityFailure.
    RetryOptions retry = RetryOptions.newBuilder()
        .setInitialInterval(Duration.ofSeconds(1))
        .setMaximumInterval(Duration.ofSeconds(30))
        .setBackoffCoefficient(2.0)
        .setMaximumAttempts(5)
        .build();

    ActivityOptions options = ActivityOptions.newBuilder()
        .setStartToCloseTimeout(Duration.ofSeconds(30))
        .setScheduleToCloseTimeout(Duration.ofMinutes(2))
        .setRetryOptions(retry)
        .build();

    return Workflow.newActivityStub(AccountOpeningActivities.class, options);
  }
}
