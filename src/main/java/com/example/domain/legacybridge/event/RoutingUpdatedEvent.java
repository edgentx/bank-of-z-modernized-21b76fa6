package com.example.domain.legacybridge.event;

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
    Instant effectiveDate,
    int newRuleVersion,
    Instant occurredAt
) implements DomainEvent {
    public RoutingUpdatedEvent {
        if (eventId == null) eventId = UUID.randomUUID().toString();
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
