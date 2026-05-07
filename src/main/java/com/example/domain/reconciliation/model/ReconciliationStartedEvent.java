package com.example.domain.reconciliation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a Reconciliation Batch is started.
 */
public record ReconciliationStartedEvent(
        String eventId,
        String aggregateId,
        Instant windowStart,
        Instant windowEnd,
        Instant occurredAt
) implements DomainEvent {

    public ReconciliationStartedEvent(String aggregateId, Instant windowStart, Instant windowEnd, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, windowStart, windowEnd, occurredAt);
    }

    @Override
    public String type() {
        return "reconciliation.started";
    }
}
