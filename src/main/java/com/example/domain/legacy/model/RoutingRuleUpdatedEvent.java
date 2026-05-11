package com.example.domain.legacy.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Domain event emitted when a routing rule is successfully updated.
 */
public record RoutingRuleUpdatedEvent(
        String type,
        String aggregateId,
        String ruleId,
        String newTarget,
        Instant effectiveDate,
        Instant occurredAt
) implements DomainEvent {

    public RoutingRuleUpdatedEvent(String aggregateId, String ruleId, String newTarget, Instant effectiveDate) {
        this("RoutingRuleUpdated", aggregateId, ruleId, newTarget, effectiveDate, Instant.now());
    }

    @Override
    public String type() {
        return type;
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
