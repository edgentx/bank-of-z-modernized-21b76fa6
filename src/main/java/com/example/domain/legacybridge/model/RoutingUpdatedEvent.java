package com.example.domain.legacybridge.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Event emitted when a routing rule on LegacyTransactionRoute is updated. S-24.
 */
public record RoutingUpdatedEvent(
        String aggregateId,
        String newTarget,
        Instant effectiveDate,
        int appliedVersion,
        Instant occurredAt,
        String eventId
) implements DomainEvent {

    public RoutingUpdatedEvent {
        Objects.requireNonNull(aggregateId, "aggregateId required");
        Objects.requireNonNull(newTarget, "newTarget required");
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
