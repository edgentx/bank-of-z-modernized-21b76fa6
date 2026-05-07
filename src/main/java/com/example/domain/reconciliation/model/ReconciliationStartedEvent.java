package com.example.domain.reconciliation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a reconciliation batch is started.
 */
public record ReconciliationStartedEvent(
        String eventId,
        String aggregateId,
        Instant batchStart,
        Instant batchEnd,
        Instant occurredAt
) implements DomainEvent {
    public ReconciliationStartedEvent(String aggregateId, Instant batchStart, Instant batchEnd, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, batchStart, batchEnd, occurredAt);
    }

    @Override
    public String type() {
        return "reconciliation.started";
    }
}
