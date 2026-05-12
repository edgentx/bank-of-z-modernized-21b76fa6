package com.example.infrastructure.temporal.saga;

/**
 * BANK S-33 — terminal result of a legacy-transaction routing workflow.
 *
 * <p>{@code chosenRoute} reflects the routing decision the aggregate made
 * ({@code "MODERN"} or {@code "LEGACY"}) so callers can audit which leg
 * actually executed. {@code checkpointId} is populated when the workflow
 * recorded a data-sync checkpoint after the legacy CICS/IMS call returned.
 */
public record LegacyTransactionRoutingResult(
    boolean success,
    String routeId,
    String chosenRoute,
    String checkpointId,
    String failureReason) {

  public static LegacyTransactionRoutingResult succeeded(
      String routeId, String chosenRoute, String checkpointId) {
    return new LegacyTransactionRoutingResult(true, routeId, chosenRoute, checkpointId, "");
  }

  public static LegacyTransactionRoutingResult failed(
      String routeId, String chosenRoute, String reason) {
    return new LegacyTransactionRoutingResult(false, routeId, chosenRoute, "", reason);
  }
}
