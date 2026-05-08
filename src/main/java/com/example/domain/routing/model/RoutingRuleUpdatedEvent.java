package com.example.domain.routing.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

/**
 * Event emitted when a routing rule is successfully updated.
 */
public record RoutingRuleUpdatedEvent(
        String aggregateId,
        String ruleId,
        String newTarget,
        int ruleVersion,
        Instant occurredAt
) implements DomainEvent {
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
