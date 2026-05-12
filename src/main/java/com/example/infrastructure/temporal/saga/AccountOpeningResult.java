package com.example.infrastructure.temporal.saga;

/**
 * BANK S-33 — terminal result of an account-opening saga.
 *
 * <p>Returned from the {@code @WorkflowMethod}; serialized via the Temporal
 * payload converter. {@code success} indicates whether the saga reached its
 * happy path — when {@code false}, {@code compensationLog} contains a
 * human-readable trail of which compensation steps fired (in order) so
 * operators can reconcile partial state.
 *
 * @param success           true if the workflow committed all three steps
 *                          (enroll customer, open account, post deposit)
 * @param accountId         account id created (or attempted)
 * @param customerId        customer id used or enrolled
 * @param transactionId     id of the initial-deposit transaction (empty on
 *                          failure before step 3)
 * @param compensationLog   chronological list of compensation step descriptors
 *                          (empty when {@code success=true})
 * @param failureReason     short error message; empty when successful
 */
public record AccountOpeningResult(
    boolean success,
    String accountId,
    String customerId,
    String transactionId,
    java.util.List<String> compensationLog,
    String failureReason) {

  public static AccountOpeningResult succeeded(String accountId, String customerId, String txId) {
    return new AccountOpeningResult(true, accountId, customerId, txId, java.util.List.of(), "");
  }

  public static AccountOpeningResult failed(
      String accountId, String customerId, String txId,
      java.util.List<String> compensationLog, String reason) {
    return new AccountOpeningResult(false, accountId, customerId, txId,
        java.util.List.copyOf(compensationLog), reason);
  }
}
