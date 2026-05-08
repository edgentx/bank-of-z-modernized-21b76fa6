package com.example.domain.legacybridge.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a routing rule is successfully updated.
 */
public record RoutingRuleUpdatedEvent(
        String eventId,
        String aggregateId,
        String ruleId,
        String newTarget,
        int newVersion,
        Instant occurredAt
) implements DomainEvent {
    public RoutingRuleUpdatedEvent {
        if (eventId == null) eventId = UUID.randomUUID().toString();
    }

    public RoutingRuleUpdatedEvent(String aggregateId, String ruleId, String newTarget, int newVersion, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, ruleId, newTarget, newVersion, occurredAt);
    }

    @Override
    public String type() {
        return "routing.updated";
    }
}
