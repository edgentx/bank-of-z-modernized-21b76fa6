package com.example.domain.legacybridge.model;

import com.example.domain.shared.Command;

import java.time.Instant;
import java.util.Objects;

/**
 * Command to update a routing rule, shifting traffic from legacy to modern.
 * S-24: UpdateRoutingRuleCmd
 */
public record UpdateRoutingRuleCmd(
    String routeId,
    String ruleId,
    String newTarget,
    Instant effectiveDate
) implements Command {

    public UpdateRoutingRuleCmd {
        Objects.requireNonNull(routeId, "routeId is required");
        Objects.requireNonNull(ruleId, "ruleId is required");
        Objects.requireNonNull(newTarget, "newTarget is required");
        Objects.requireNonNull(effectiveDate, "effectiveDate is required");
    }
}
