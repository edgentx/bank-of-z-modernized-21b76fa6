package com.example.domain.legacy.event;

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
    int newVersion,
    Instant occurredAt
) implements DomainEvent {
    public RoutingUpdatedEvent(String aggregateId, String ruleId, String newTarget, Instant effectiveDate, int newVersion, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, ruleId, newTarget, effectiveDate, newVersion, occurredAt);
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
