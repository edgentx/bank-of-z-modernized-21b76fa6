package com.example.infrastructure.telemetry;

import io.opentelemetry.api.common.AttributeKey;

/**
 * BANK S-35 — span attribute keys that identify the legacy CICS/IMS pathway
 * a particular operation is bridging into.
 *
 * <p>The acceptance criterion "Trace context includes CICS/IMS pathway
 * identifiers" means a span emitted by a request that crossed the
 * modernized → CICS/IMS boundary must carry enough context for an operator
 * to correlate the modern trace with the mainframe's own transaction log.
 * The keys below are the canonical four pieces of pathway state we have
 * available from the legacy-bridge bounded context:
 *
 * <ul>
 *   <li>{@link #CICS_TRANSACTION_ID} — the 4-character CICS TRANSID
 *       (e.g. {@code "ACCT"}) the request was dispatched as;</li>
 *   <li>{@link #CICS_PROGRAM_NAME} — the COBOL program inside CICS that
 *       handled the transaction (the {@code XCTL} target);</li>
 *   <li>{@link #IMS_TRANSACTION_CODE} — when the route lands on IMS, the
 *       8-character IMS transaction code that selected the MPR;</li>
 *   <li>{@link #LEGACY_ROUTE_ID} — the modernized-side
 *       {@code LegacyTransactionRoute} aggregate id, so the trace can
 *       cross-reference the routing decision aggregate event.</li>
 * </ul>
 *
 * <p>Keys are exposed as constants (not redefined per call site) so a typo
 * never silently produces a divergent attribute on one span — the compiler
 * catches it. The {@code legacy.cics.*} / {@code legacy.ims.*} namespace
 * keeps custom attributes from colliding with the OpenTelemetry semantic
 * conventions {@code messaging.*} / {@code db.*} families.
 */
public final class CicsImsAttributes {

  /** Modernized-side LegacyTransactionRoute aggregate id. */
  public static final AttributeKey<String> LEGACY_ROUTE_ID =
      AttributeKey.stringKey("legacy.route.id");

  /** CICS TRANSID (4-char) the call was dispatched as. */
  public static final AttributeKey<String> CICS_TRANSACTION_ID =
      AttributeKey.stringKey("legacy.cics.transaction_id");

  /** COBOL program name inside CICS that handled the transaction. */
  public static final AttributeKey<String> CICS_PROGRAM_NAME =
      AttributeKey.stringKey("legacy.cics.program_name");

  /** IMS transaction code (8-char) selecting the MPR. */
  public static final AttributeKey<String> IMS_TRANSACTION_CODE =
      AttributeKey.stringKey("legacy.ims.transaction_code");

  /**
   * Routing decision — one of {@code MODERN}, {@code CICS}, {@code IMS}.
   * Carries which side of the strangler-fig boundary handled the request.
   */
  public static final AttributeKey<String> LEGACY_PATHWAY =
      AttributeKey.stringKey("legacy.pathway");

  private CicsImsAttributes() {
    // utility class — no instances
  }
}
