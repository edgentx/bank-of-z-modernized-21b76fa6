package com.example.infrastructure.temporal.activity;

import com.example.application.legacybridge.LegacyBridgeAppService;
import com.example.domain.legacybridge.model.EvaluateRoutingCmd;
import com.example.domain.legacybridge.model.LegacyTransactionRoute;
import com.example.domain.legacybridge.model.RecordSyncCheckpointCmd;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * BANK S-33 — concrete activity implementations for the legacy-bridge
 * CICS/IMS routing workflow.
 *
 * <p>{@link #evaluateRouting} delegates to {@link LegacyBridgeAppService}
 * which owns the {@link LegacyTransactionRoute} aggregate. Once the legacy
 * bridge gains its persistence adapter only this class needs to change —
 * the workflow keeps the same shape.
 *
 * <p>{@link #invokeCicsTransaction} is currently a stub returning a synthetic
 * confirmation id; the MQ-bridge integration lands in a follow-up story. The
 * stub exists so the surrounding workflow + retry policy can be exercised
 * end-to-end against the test workflow environment.
 */
@Component
public class LegacyBridgeActivitiesImpl implements LegacyBridgeActivities {

  private final LegacyBridgeAppService legacyBridgeService;

  public LegacyBridgeActivitiesImpl(LegacyBridgeAppService legacyBridgeService) {
    this.legacyBridgeService = legacyBridgeService;
  }

  @Override
  public String evaluateRouting(String routeId, String sourceSystem,
                                String transactionType, String accountNumber,
                                long amountCents, String currency) {
    Map<String, Object> payload = new HashMap<>();
    payload.put("sourceSystem", sourceSystem);
    payload.put("accountNumber", accountNumber);
    payload.put("amountCents", amountCents);
    payload.put("currency", currency);
    // The aggregate routes to MODERN whenever the payload carries the
    // forceModern marker. The workflow chooses to set that based on the
    // sourceSystem the caller declared, so MODERN callers don't get
    // accidentally bridged into the legacy CICS/IMS path.
    if ("MODERN".equalsIgnoreCase(sourceSystem)) {
      payload.put("forceModern", true);
    }

    EvaluateRoutingCmd cmd = new EvaluateRoutingCmd(routeId, transactionType, payload, 1);
    LegacyTransactionRoute route = legacyBridgeService.evaluateRouting(cmd);
    return route.getTargetSystem();
  }

  @Override
  public String invokeCicsTransaction(String routeId, String accountNumber,
                                      String transactionType, long amountCents, String currency) {
    // Bridge stub — the MQ-fronted CICS/IMS call lands in a follow-up story.
    // Returning a deterministic synthetic confirmation lets the workflow
    // proceed and the data-parity checkpoint be exercised end-to-end in
    // tests. The format mirrors what the production MQ adapter will emit.
    return "CICS-CONF-" + routeId;
  }

  @Override
  public String recordSyncCheckpoint(String checkpointId, String sourceSystem,
                                     long recordsProcessed, long lastSequenceNumber) {
    // The aggregate validates checkpoint required-fields; we feed it a
    // synthetic validation hash derived from the inputs so re-running the
    // activity with the same args yields the same hash (idempotent under
    // Temporal retries).
    String hash = sourceSystem + ":" + recordsProcessed + ":" + lastSequenceNumber;
    RecordSyncCheckpointCmd cmd = new RecordSyncCheckpointCmd(checkpointId, lastSequenceNumber, hash);
    legacyBridgeService.recordCheckpoint(cmd);
    return checkpointId;
  }
}
