package com.example.domain.legacybridge.model;

import com.example.domain.shared.Command;

import java.time.Instant;
import java.util.Objects;

/**
 * Command to update routing rules for a legacy transaction route.
 * Used to shift traffic from legacy to modern systems.
 */
public record UpdateRoutingRuleCmd(
    String ruleId,
    String newTarget,
    Instant effectiveDate
) implements Command {

    public UpdateRoutingRuleCmd {
        Objects.requireNonNull(ruleId, "ruleId cannot be null");
        Objects.requireNonNull(newTarget, "newTarget cannot be null");
        Objects.requireNonNull(effectiveDate, "effectiveDate cannot be null");
    }
}