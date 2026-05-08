package com.example.domain.legacybridge.model;

import com.example.domain.shared.Command;
import java.time.Instant;

/**
 * Command to update the routing rule for a specific transaction route.
 * Used to shift traffic from Legacy (CICS/IMS) to Modern (VForce360) systems.
 */
public record UpdateRoutingRuleCmd(
        String routeId,
        String ruleId,
        String newTarget, // e.g. "MODERN" or "LEGACY"
        Instant effectiveDate,
        int newVersion
) implements Command {}
