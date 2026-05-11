package com.example.domain.legacybridge.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Objects;

/**
 * Event emitted when a routing rule is successfully updated.
 */
public record RoutingUpdatedEvent(
    String aggregateId,
    String ruleId,
    String newTarget,
    Instant effectiveDate,
    Instant occurredAt
) implements DomainEvent {

    public RoutingUpdatedEvent {
        Objects.requireNonNull(aggregateId);
        Objects.requireNonNull(occurredAt);
    }

    @Override
    public String type() {
        return "routing.updated";
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