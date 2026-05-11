package com.example.domain.legacybridge.model;

import com.example.domain.shared.Command;
import java.time.Instant;
import java.util.Objects;

/**
 * Command to update an existing routing rule.
 * Used to shift traffic from Legacy to Modern systems.
 */
public record UpdateRoutingRuleCmd(
    String routeId,
    String ruleId,
    String newTarget,
    Instant effectiveDate
) implements Command {

    public UpdateRoutingRuleCmd {
        Objects.requireNonNull(routeId);
        Objects.requireNonNull(ruleId);
        Objects.requireNonNull(newTarget);
        Objects.requireNonNull(effectiveDate);
        
        if (routeId.isBlank()) throw new IllegalArgumentException("routeId cannot be blank");
        if (newTarget.isBlank()) throw new IllegalArgumentException("newTarget cannot be blank");
    }
}