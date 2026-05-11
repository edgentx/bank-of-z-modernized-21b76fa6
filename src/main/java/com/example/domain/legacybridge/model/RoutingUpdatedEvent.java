package com.example.domain.legacybridge.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a routing rule is successfully updated.
 */
public record RoutingUpdatedEvent(
        String eventId,
        String routeId,
        String ruleId,
        String newTarget,
        Instant effectiveDate,
        int version,
        Instant occurredAt
) implements DomainEvent {
    public RoutingUpdatedEvent {
        if (eventId == null) eventId = UUID.randomUUID().toString();
    }

    @Override
    public String type() {
        return "RoutingUpdated";
    }

    @Override
    public String aggregateId() {
        return routeId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
