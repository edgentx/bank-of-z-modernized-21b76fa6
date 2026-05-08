package com.example.domain.legacybridge.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Event emitted when a routing rule is successfully updated.
 */
public record RuleUpdatedEvent(
        String aggregateId,
        String ruleId,
        String targetSystem,
        Instant effectiveDate,
        Instant occurredAt
) implements DomainEvent {

    public RuleUpdatedEvent {
        Objects.requireNonNull(aggregateId, "aggregateId cannot be null");
        Objects.requireNonNull(targetSystem, "targetSystem cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
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
