package com.example.domain.legacybridge.model;

import com.example.domain.shared.Command;
import java.time.Instant;
import java.util.Objects;

/**
 * Command to update a routing rule, shifting traffic from Legacy to Modern systems.
 * S-24 Implementation.
 */
public record UpdateRoutingRuleCmd(
        String routeId,
        String ruleId,
        String newTarget,
        Instant effectiveDate,
        int newVersion
) implements Command {

    public UpdateRoutingRuleCmd {
        Objects.requireNonNull(routeId, "routeId cannot be null");
        Objects.requireNonNull(ruleId, "ruleId cannot be null");
        Objects.requireNonNull(newTarget, "newTarget cannot be null");
        Objects.requireNonNull(effectiveDate, "effectiveDate cannot be null");
        if (routeId.isBlank()) throw new IllegalArgumentException("routeId cannot be blank");
        if (newTarget.isBlank()) throw new IllegalArgumentException("newTarget cannot be blank");
    }
}
