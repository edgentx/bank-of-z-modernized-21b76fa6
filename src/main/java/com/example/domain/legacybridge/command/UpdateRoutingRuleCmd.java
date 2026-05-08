package com.example.domain.legacybridge.command;

import com.example.domain.shared.Command;

import java.time.Instant;
import java.util.Objects;

/**
 * Command to update the routing rule for a Legacy Transaction Route.
 * Used to shift traffic from Legacy to Modern systems.
 */
public record UpdateRoutingRuleCmd(
        String ruleId,
        String newTarget,
        Instant effectiveDate
) implements Command {
    public UpdateRoutingRuleCmd {
        Objects.requireNonNull(ruleId, "ruleId is required");
        Objects.requireNonNull(newTarget, "newTarget is required");
        Objects.requireNonNull(effectiveDate, "effectiveDate is required");
    }
}