package com.example.domain.reconciliation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Domain event emitted when a reconciliation batch process is started.
 */
public record ReconciliationStartedEvent(
        String aggregateId,
        Instant batchWindowStart,
        Instant batchWindowEnd,
        Instant occurredAt
) implements DomainEvent {

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
