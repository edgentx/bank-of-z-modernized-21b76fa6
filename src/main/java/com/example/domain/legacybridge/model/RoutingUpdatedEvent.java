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
        int newVersion,
        Instant effectiveDate,
        Instant occurredAt
) implements DomainEvent {
    public RoutingUpdatedEvent {
        if (eventId == null) eventId = UUID.randomUUID().toString();
        if (occurredAt == null) occurredAt = Instant.now();
    }

    public RoutingUpdatedEvent(String aggregateId, String ruleId, String newTarget, int newVersion, Instant effectiveDate) {
        this(UUID.randomUUID().toString(), aggregateId, ruleId, newTarget, newVersion, effectiveDate, Instant.now());
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
        return occurredAt;
    }
}
