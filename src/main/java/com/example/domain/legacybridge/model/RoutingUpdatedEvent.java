package com.example.domain.legacybridge.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Event emitted when a routing rule is successfully updated.
 * Part of Story S-24.
 */
public record RoutingUpdatedEvent(
    String aggregateId,
    String ruleId,
    String newTarget,
    Instant effectiveDate,
    Instant occurredAt
) implements DomainEvent {

    public RoutingUpdatedEvent {
        Objects.requireNonNull(aggregateId, "aggregateId cannot be null");
        Objects.requireNonNull(ruleId, "ruleId cannot be null");
        Objects.requireNonNull(newTarget, "newTarget cannot be null");
        Objects.requireNonNull(effectiveDate, "effectiveDate cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
    }

    @Override
    public String type() {
        return "routing.updated";
    }
}