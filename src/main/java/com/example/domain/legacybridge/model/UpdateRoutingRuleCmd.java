package com.example.domain.legacybridge.model;

import com.example.domain.shared.Command;
import java.time.Instant;

/**
 * Command to update an existing routing rule configuration.
 * Used to shift traffic from legacy to modern systems or vice versa.
 */
public record UpdateRoutingRuleCmd(
        String routeId,
        String ruleId,
        String newTarget,
        Instant effectiveDate
) implements Command {}
