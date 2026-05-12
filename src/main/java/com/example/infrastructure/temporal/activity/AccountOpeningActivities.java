package com.example.infrastructure.temporal.activity;

import io.temporal.activity.ActivityInterface;

/**
 * BANK S-33 — Temporal activity contract for the account-opening saga.
 *
 * <p>Each method represents a single state-changing step (the kind that
 * must be retried with exponential backoff and compensated on failure). The
 * implementation delegates to the existing {@code CustomerAppService},
 * {@code AccountAppService}, and {@code TransactionAppService} — no domain
 * logic lives in the activity adapter.
 *
 * <p>The compensation methods ({@code closeAccount}, {@code reverseTransaction})
 * are also activities so the workflow's saga can invoke them under the same
 * retry policy as the forward path — durable compensation is the entire
 * point of using Temporal here, so a flaky transient failure on compensation
 * must not orphan partial state.
 *
 * <p>Methods are intentionally NOT annotated with {@code @ActivityMethod}.
 * Temporal auto-discovers every public method on an {@link ActivityInterface}-marked
 * type, and putting the annotation on individual methods causes
 * {@code Mockito.mock(...)} of this interface to fail registration (the
 * mock subclass inherits the annotation, and Temporal refuses to scan
 * non-interface methods that carry it). We accept Temporal's default
 * activity-name derivation (first letter uppercased) in exchange for
 * trivial mocking in tests.
 */
@ActivityInterface
public interface AccountOpeningActivities {

  /**
   * Enroll a customer if one with {@code customerId} does not already
   * exist. Idempotent — re-running this activity on the same id is a no-op
   * once the customer is enrolled.
   *
   * @return the customer id (echoed back so the workflow can correlate)
   */
  String enrollCustomer(String customerId, String firstName, String lastName, String email);

  /**
   * Open an account aggregate for {@code customerId}. Idempotent on
   * {@code accountId} — the aggregate rejects re-open attempts via its
   * uniqueness invariant, so the activity catches that path and returns
   * the existing account id rather than failing.
   */
  String openAccount(String accountId, String customerId, String accountType,
                     long initialDepositCents, String sortCode);

  /**
   * Post the initial deposit transaction. The workflow seeds the
   * {@code transactionId} from {@code Workflow.randomUUID()} so retries are
   * idempotent — the activity does not mint a new id internally.
   */
  String postInitialDeposit(String transactionId, String accountNumber,
                            long amountCents, String currency);

  // -- compensations -----------------------------------------------------------

  /**
   * Reverse a previously-posted transaction. The activity uses the existing
   * {@code ReverseTransactionCmd} on the transaction aggregate which appends
   * a compensating {@code transaction.reversed} event rather than deleting
   * the original — banking ledgers are append-only.
   */
  void reverseTransaction(String transactionId, String reason);

  /**
   * Close an account opened earlier in the saga. Used when {@code postInitialDeposit}
   * fails after the account was created — the account is closed via
   * {@code CloseAccountCmd} so it never leaves a half-open state visible to
   * downstream consumers.
   */
  void closeAccount(String accountId, String reason);
}
