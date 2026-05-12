package com.example.infrastructure.temporal.saga;

/**
 * BANK S-33 — input payload for the legacy-bridge transaction routing
 * workflow (CICS/IMS orchestration).
 *
 * <p>Wraps a {@code routeId} plus the parameters needed by the
 * {@code EvaluateRoutingCmd} on {@code LegacyTransactionRoute}. The workflow
 * decides between the modernized path and the legacy CICS/IMS bridge based
 * on the aggregate's routing rule, then either dispatches the modern
 * activity or invokes the CICS call with the configured retry policy.
 *
 * @param routeId             deterministic id supplied by the caller for
 *                            idempotency across workflow retries
 * @param sourceSystem        e.g. {@code "MODERN"}, {@code "CICS"}, {@code "IMS"}
 * @param transactionType     e.g. {@code "DEPOSIT"}, {@code "WITHDRAW"}
 * @param accountNumber       target account for the routed transaction
 * @param amountCents         transaction amount in the smallest currency unit
 * @param currency            ISO-4217 code
 */
public record LegacyTransactionRoutingInput(
    String routeId,
    String sourceSystem,
    String transactionType,
    String accountNumber,
    long amountCents,
    String currency) {
}
