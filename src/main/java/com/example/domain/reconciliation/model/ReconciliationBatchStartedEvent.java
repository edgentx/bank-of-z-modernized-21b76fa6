package com.example.domain.reconciliation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a reconciliation batch process is successfully started.
 */
public record ReconciliationBatchStartedEvent(
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
