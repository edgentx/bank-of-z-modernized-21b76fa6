package com.example.domain.legacybridge.model;

import com.example.domain.shared.Command;

import java.time.Instant;
import java.util.Objects;

/**
 * Command to update a routing rule on the LegacyTransactionRoute aggregate.
 * Shifts traffic from legacy to modern systems by replacing the active rule
 * with a new versioned target effective at the supplied instant. S-24.
 */
public record UpdateRoutingRuleCmd(
        String ruleId,
        String newTarget,
        Instant effectiveDate,
        int rulesVersion
) implements Command {
    public UpdateRoutingRuleCmd {
        Objects.requireNonNull(ruleId, "ruleId required");
    }
}
