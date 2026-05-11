package com.example.domain.legacybridge.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Domain event emitted when a routing rule is successfully updated.
 */
public record RoutingUpdatedEvent(
        String aggregateId,
        String ruleId,
        String oldTarget,
        String newTarget,
        int effectiveVersion,
        Instant updatedDate
) implements DomainEvent {

    @Override
    public String type() {
        return "RoutingUpdated";
    }

    @Override
    public String aggregateId() {
        return aggregateId();
    }

    @Override
    public Instant occurredAt() {
        return Instant.now(); // Or use a timestamp passed in constructor if precise timing is needed
    }
}
