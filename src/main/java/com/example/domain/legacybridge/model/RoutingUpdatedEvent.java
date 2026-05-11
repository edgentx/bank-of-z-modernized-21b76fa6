package com.example.domain.legacybridge.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Domain event emitted when a routing rule is successfully updated.
 */
public record RoutingUpdatedEvent(
        String aggregateId,
        String ruleId,
        String newTarget,
        int newVersion,
        Instant effectiveDate,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "RoutingUpdated";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
