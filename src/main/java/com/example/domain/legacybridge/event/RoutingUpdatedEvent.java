package com.example.domain.legacybridge.event;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Domain event emitted when a routing rule is successfully updated.
 */
public record RoutingUpdatedEvent(
        String aggregateId,
        String ruleId,
        String newTarget,
        Instant effectiveDate,
        Instant occurredAt
) implements DomainEvent {
    public RoutingUpdatedEvent {
        Objects.requireNonNull(aggregateId, "aggregateId is required");
        Objects.requireNonNull(ruleId, "ruleId is required");
        Objects.requireNonNull(newTarget, "newTarget is required");
    }

    @Override
    public String type() {
        return "routing.updated";
    }
}