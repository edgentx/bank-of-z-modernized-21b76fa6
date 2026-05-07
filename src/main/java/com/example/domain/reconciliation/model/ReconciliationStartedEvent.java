package com.example.domain.reconciliation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a reconciliation batch has successfully started.
 * Used in S-16.
 */
public record ReconciliationStartedEvent(
        String eventId,
        String aggregateId,
        Instant batchWindowStart,
        Instant batchWindowEnd,
        Instant occurredAt
) implements DomainEvent {
    public ReconciliationStartedEvent {
        if (eventId == null) eventId = UUID.randomUUID().toString();
    }

    public ReconciliationStartedEvent(String aggregateId, Instant start, Instant end, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, start, end, occurredAt);
    }

    @Override
    public String type() {
        return "reconciliation.started";
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}
