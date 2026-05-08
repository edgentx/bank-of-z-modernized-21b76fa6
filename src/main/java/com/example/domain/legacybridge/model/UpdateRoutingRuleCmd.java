package com.example.domain.legacybridge.model;

import com.example.domain.shared.Command;
import java.time.Instant;

/**
 * Command to update an existing routing rule configuration.
 * Used to shift traffic from legacy to modern systems.
 */
public record UpdateRoutingRuleCmd(
        String routeId,
        String ruleId,
        String newTarget,
        int newVersion,
        Instant effectiveDate
) implements Command {
    public UpdateRoutingRuleCmd {
        if (routeId == null || routeId.isBlank()) {
            throw new IllegalArgumentException("routeId cannot be blank");
        }
        if (ruleId == null || ruleId.isBlank()) {
            throw new IllegalArgumentException("ruleId cannot be blank");
        }
        if (newTarget == null || newTarget.isBlank()) {
            throw new IllegalArgumentException("newTarget cannot be blank");
        }
        if (effectiveDate == null) {
            throw new IllegalArgumentException("effectiveDate cannot be null");
        }
    }
}
