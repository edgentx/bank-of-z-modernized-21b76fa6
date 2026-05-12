package com.example.infrastructure.temporal.workflow;

import com.example.infrastructure.temporal.saga.AccountOpeningInput;
import com.example.infrastructure.temporal.saga.AccountOpeningResult;
import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * BANK S-33 — Temporal workflow contract for the account-opening saga.
 *
 * <p>The saga sequences three state-changing steps:
 * <ol>
 *   <li>enroll the customer (if new),</li>
 *   <li>open the account aggregate,</li>
 *   <li>post the initial deposit transaction.</li>
 * </ol>
 *
 * <p>If any step fails after a previous step has committed, the workflow
 * runs registered compensations in reverse order — see
 * {@link AccountOpeningWorkflowImpl} for the Temporal {@code Saga} wiring.
 *
 * <p>The {@link #currentStep()} query method satisfies the AC "Workflow
 * query methods support status checking" — operators can ask a running
 * workflow which step it is currently on without waiting for completion.
 */
@WorkflowInterface
public interface AccountOpeningWorkflow {

  /**
   * Workflow entry point. Returns the saga's terminal result; a thrown
   * exception escapes only when compensation itself fails (otherwise the
   * workflow returns {@code success=false} with a populated
   * {@code compensationLog}).
   */
  @WorkflowMethod
  AccountOpeningResult openAccount(AccountOpeningInput input);

  /**
   * Query method — returns a short label for the step currently executing
   * (e.g. {@code "ENROLLING_CUSTOMER"}, {@code "OPENING_ACCOUNT"},
   * {@code "POSTING_DEPOSIT"}, {@code "COMPENSATING"}, {@code "DONE"}).
   * Safe to call any time; Temporal serializes query handlers against the
   * workflow's deterministic event loop.
   */
  @QueryMethod
  String currentStep();
}
