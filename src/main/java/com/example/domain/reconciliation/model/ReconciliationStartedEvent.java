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
        Instant occurredAt,
        Instant batchWindowStart,
        Instant batchWindowEnd,
        String operatorId
) implements DomainEvent {

    public ReconciliationStartedEvent(String aggregateId, Instant windowStart, Instant windowEnd, String operatorId, Instant occurredAt) {
        this(
                UUID.randomUUID().toString(),
                aggregateId,
                occurredAt,
                windowStart,
                windowEnd,
                operatorId
        );
    }

    @Override
    public String type() {
        return "reconciliation.started";
    }
}