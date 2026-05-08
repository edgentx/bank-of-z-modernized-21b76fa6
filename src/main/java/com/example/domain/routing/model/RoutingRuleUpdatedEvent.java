package com.example.domain.routing.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

/**
 * Domain event emitted when a routing rule is successfully updated.
 * Indicates a shift in traffic configuration (e.g., Legacy to Modern).
 */
public record RoutingRuleUpdatedEvent(
    String aggregateId,
    String ruleId,
    String newTarget,
    Instant effectiveDate,
    int newVersion,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "routing.updated";
    }

    @Override
    public String aggregateId() {
        return aggregateId();
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}