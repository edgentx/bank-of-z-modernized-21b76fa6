package com.example.domain.legacybridge.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a routing rule is successfully updated.
 */
public record RoutingUpdatedEvent(
        String eventId,
        String aggregateId,
        String ruleId,
        String newTarget,
        Instant effectiveDate,
        Instant occurredAt
) implements DomainEvent {
    public RoutingUpdatedEvent {
        Objects.requireNonNull(eventId);
        Objects.requireNonNull(aggregateId);
        Objects.requireNonNull(ruleId);
        Objects.requireNonNull(newTarget);
        Objects.requireNonNull(effectiveDate);
        Objects.requireNonNull(occurredAt);
    }

    public RoutingUpdatedEvent(String aggregateId, String ruleId, String newTarget, Instant effectiveDate, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, ruleId, newTarget, effectiveDate, occurredAt);
    }

    @Override
    public String type() {
        return "RoutingUpdatedEvent";
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
