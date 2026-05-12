package com.example.infrastructure.temporal.saga;

/**
 * BANK S-33 — input payload for the account-opening saga workflow.
 *
 * <p>Carried as the {@code @WorkflowMethod} argument; serialized via the
 * Temporal payload converter (JSON by default). Records work cleanly with
 * Temporal's default Jackson-based converter so we do not need a custom
 * {@code PayloadConverter} for this DTO.
 *
 * <p>{@code initialDepositCents} is the smallest unit (e.g. cents/pence) so
 * we never carry doubles across the workflow boundary.
 *
 * @param customerId       existing or to-be-enrolled customer id
 * @param firstName        customer first name (used only when customer is new)
 * @param lastName         customer last name (used only when customer is new)
 * @param email            customer email (used only when customer is new)
 * @param accountId        deterministic account id supplied by the caller so
 *                         retries stay idempotent (otherwise we'd open two
 *                         accounts on the second attempt)
 * @param accountType      e.g. {@code "CHECKING"}, {@code "SAVINGS"}
 * @param sortCode         legacy CICS routing code
 * @param initialDepositCents seed deposit, in the smallest currency unit
 * @param currency         ISO-4217 code, e.g. {@code "USD"}
 */
public record AccountOpeningInput(
    String customerId,
    String firstName,
    String lastName,
    String email,
    String accountId,
    String accountType,
    String sortCode,
    long initialDepositCents,
    String currency) {
}
