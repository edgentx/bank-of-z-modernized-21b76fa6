package com.example.domain.routing.model;

import com.example.domain.shared.Command;

import java.time.Instant;
import java.util.Objects;

/**
 * Command to update a routing rule.
 * Used in S-24 to shift traffic from legacy to modern systems.
 */
public record UpdateRoutingRuleCmd(
    String aggregateId,
    String newTarget,
    Instant effectiveDate
) implements Command {

    public UpdateRoutingRuleCmd {
        Objects.requireNonNull(aggregateId, "aggregateId required");
        Objects.requireNonNull(newTarget, "newTarget required");
        Objects.requireNonNull(effectiveDate, "effectiveDate required");
        if (aggregateId.isBlank()) throw new IllegalArgumentException("aggregateId cannot be blank");
        if (newTarget.isBlank()) throw new IllegalArgumentException("newTarget cannot be blank");
    }
}
