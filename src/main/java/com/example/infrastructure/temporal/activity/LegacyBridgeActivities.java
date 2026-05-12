package com.example.infrastructure.temporal.activity;

import io.temporal.activity.ActivityInterface;

/**
 * BANK S-33 — Temporal activity contract for CICS/IMS legacy bridge
 * orchestration.
 *
 * <p>Activity bodies delegate to {@link com.example.application.legacybridge.LegacyBridgeAppService}
 * which owns the routing aggregate. The workflow uses these activities to
 * decouple itself from the in-memory state map inside the app service — once
 * the legacy-bridge bounded context gains a persistence adapter, only the
 * activity adapter changes, not the workflow.
 *
 * <p>Methods are intentionally NOT annotated with {@code @ActivityMethod}
 * (see {@link AccountOpeningActivities} for the rationale).
 */
@ActivityInterface
public interface LegacyBridgeActivities {

  /**
   * Evaluate the routing rule on the {@code LegacyTransactionRoute}
   * aggregate. Returns the route name the aggregate selected
   * ({@code "MODERN"} or {@code "LEGACY"}).
   */
  String evaluateRouting(String routeId, String sourceSystem,
                         String transactionType, String accountNumber,
                         long amountCents, String currency);

  /**
   * Invoke the legacy CICS/IMS transaction. In production this fronts the
   * MQ-bridge call; for now (the legacy bridge is still in the modernization
   * window) it is a stub that returns a synthetic confirmation id. The
   * activity exists so the workflow's retry policy applies uniformly to all
   * legacy I/O paths once the real call lands.
   */
  String invokeCicsTransaction(String routeId, String accountNumber,
                               String transactionType, long amountCents, String currency);

  /**
   * Record a data-sync checkpoint so the parity verifier knows where to pick
   * up. Idempotent on {@code checkpointId}.
   */
  String recordSyncCheckpoint(String checkpointId, String sourceSystem,
                              long recordsProcessed, long lastSequenceNumber);
}
