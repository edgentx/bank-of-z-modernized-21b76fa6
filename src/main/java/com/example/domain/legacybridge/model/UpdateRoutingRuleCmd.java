package com.example.domain.legacybridge.model;

import com.example.domain.shared.Command;
import java.time.Instant;

/**
 * Command to update a specific routing rule.
 * Used to shift traffic from legacy to modern systems.
 */
public record UpdateRoutingRuleCmd(
        String routeId,
        String ruleId,
        String newTarget,
        int newVersion,
        Instant effectiveDate
) implements Command {
}