package com.example.infrastructure.telemetry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.opentelemetry.api.common.AttributeType;
import org.junit.jupiter.api.Test;

/**
 * BANK S-35 — pins the {@link CicsImsAttributes} key names.
 *
 * <p>Operators dashboard on these exact attribute keys, so accidental
 * renames (e.g. {@code legacy.route.id} → {@code legacy.routeId}) silently
 * break Grafana panels and alert routing. Pin the wire-level names here
 * so the compiler catches drift.
 */
class CicsImsAttributesTest {

  @Test
  void keyNamesArePinned() {
    assertEquals("legacy.route.id", CicsImsAttributes.LEGACY_ROUTE_ID.getKey());
    assertEquals("legacy.cics.transaction_id", CicsImsAttributes.CICS_TRANSACTION_ID.getKey());
    assertEquals("legacy.cics.program_name", CicsImsAttributes.CICS_PROGRAM_NAME.getKey());
    assertEquals("legacy.ims.transaction_code", CicsImsAttributes.IMS_TRANSACTION_CODE.getKey());
    assertEquals("legacy.pathway", CicsImsAttributes.LEGACY_PATHWAY.getKey());
  }

  @Test
  void keysAreStringTyped() {
    // CICS/IMS transaction IDs are opaque short strings on the mainframe
    // (4 chars for CICS TRANSID, 8 chars for IMS transaction code). Pin
    // the SDK type so a future refactor cannot quietly change them to
    // {@code longKey} or {@code stringArrayKey} — the collector indexes
    // by attribute type, not just name.
    assertNotNull(CicsImsAttributes.LEGACY_ROUTE_ID);
    assertEquals(AttributeType.STRING, CicsImsAttributes.LEGACY_ROUTE_ID.getType());
    assertEquals(AttributeType.STRING, CicsImsAttributes.CICS_TRANSACTION_ID.getType());
    assertEquals(AttributeType.STRING, CicsImsAttributes.CICS_PROGRAM_NAME.getType());
    assertEquals(AttributeType.STRING, CicsImsAttributes.IMS_TRANSACTION_CODE.getType());
    assertEquals(AttributeType.STRING, CicsImsAttributes.LEGACY_PATHWAY.getType());
  }
}
