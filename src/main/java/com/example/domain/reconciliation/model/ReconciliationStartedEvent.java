package com.example.domain.reconciliation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a reconciliation batch has been started.
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

    public static ReconciliationStartedEvent create(String aggregateId, Instant batchWindowStart, Instant batchWindowEnd) {
        return new ReconciliationStartedEvent(
                UUID.randomUUID().toString(),
                aggregateId,
                batchWindowStart,
                batchWindowEnd,
                Instant.now()
        );
    }

    @Override
    public String type() {
        return "reconciliation.started";
    }
}