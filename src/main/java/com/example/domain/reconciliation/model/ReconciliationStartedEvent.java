package com.example.domain.reconciliation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a reconciliation batch is started.
 * S-16
 */
public record ReconciliationStartedEvent(
        String eventId,
        String aggregateId,
        Instant batchWindowStart,
        Instant batchWindowEnd,
        Instant occurredAt
) implements DomainEvent {
    public ReconciliationStartedEvent(String aggregateId, Instant batchWindowStart, Instant batchWindowEnd, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, batchWindowStart, batchWindowEnd, occurredAt);
    }

    @Override
    public String type() {
        return "reconciliation.started";
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