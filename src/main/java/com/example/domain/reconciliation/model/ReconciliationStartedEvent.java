package com.example.domain.reconciliation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Domain event emitted when a reconciliation batch is started.
 */
public class ReconciliationStartedEvent implements DomainEvent {
    private final String aggregateId;
    private final Instant batchWindowStart;
    private final Instant batchWindowEnd;
    private final Instant occurredAt;

    public ReconciliationStartedEvent(String aggregateId, Instant batchWindowStart, Instant batchWindowEnd, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.batchWindowStart = batchWindowStart;
        this.batchWindowEnd = batchWindowEnd;
        this.occurredAt = occurredAt;
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

    public Instant getBatchWindowStart() {
        return batchWindowStart;
    }

    public Instant getBatchWindowEnd() {
        return batchWindowEnd;
    }
}
