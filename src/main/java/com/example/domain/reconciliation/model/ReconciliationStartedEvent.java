package com.example.domain.reconciliation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a reconciliation batch process is started.
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
