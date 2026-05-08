package com.example.domain.legacybridge.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a routing rule is successfully updated.
 */
public record RoutingUpdatedEvent(
        String eventId,
        String aggregateId,
        String ruleId,
        String newTarget,
        int newRulesVersion,
        Instant occurredOn
) implements DomainEvent {
    public RoutingUpdatedEvent(String aggregateId, String ruleId, String newTarget, int newRulesVersion, Instant occurredOn) {
        this(UUID.randomUUID().toString(), aggregateId, ruleId, newTarget, newRulesVersion, occurredOn);
    }

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
        return occurredOn;
    }
}
