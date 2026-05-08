package com.example.domain.legacybridge.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

/**
 * Event emitted when a routing rule is successfully updated.
 */
public record RoutingRuleUpdatedEvent(
        String aggregateId,
        String ruleId,
        String newTarget,
        int newVersion,
        Instant effectiveDate,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "RoutingRuleUpdated";
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
