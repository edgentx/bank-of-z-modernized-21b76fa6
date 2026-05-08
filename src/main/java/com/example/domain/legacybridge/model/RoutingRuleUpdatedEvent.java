package com.example.domain.legacybridge.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a routing rule configuration is successfully updated.
 */
public record RoutingRuleUpdatedEvent(
        String eventId,
        String aggregateId,
        String ruleId,
        String newTarget,
        int newRuleVersion,
        Instant effectiveDate,
        Instant occurredAt
) implements DomainEvent {
    public RoutingRuleUpdatedEvent {
        if (eventId == null) {
            eventId = UUID.randomUUID().toString();
        }
    }

    @Override
    public String type() {
        return "routing.updated";
    }
}
