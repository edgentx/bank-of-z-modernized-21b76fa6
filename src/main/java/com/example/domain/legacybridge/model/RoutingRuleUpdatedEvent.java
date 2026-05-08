package com.example.domain.legacybridge.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

/**
 * Event emitted when a routing rule is successfully updated.
 */
public record RoutingRuleUpdatedEvent(
        String type,
        String aggregateId,
        String ruleId,
        String target,
        int version,
        Instant occurredAt
) implements DomainEvent {

    public RoutingRuleUpdatedEvent(String aggregateId, String ruleId, String target, int version, Instant occurredAt) {
        this("routing.updated", aggregateId, ruleId, target, version, occurredAt);
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
