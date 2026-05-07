package com.example.domain.reconciliation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a reconciliation batch is started.
 * Used in Story S-16.
 */
public class ReconciliationStartedEvent implements DomainEvent {
    private final String aggregateId;
    private final Instant windowStart;
    private final Instant windowEnd;
    private final Instant occurredAt;

    public ReconciliationStartedEvent(String aggregateId, Instant windowStart, Instant windowEnd, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.windowStart = windowStart;
        this.windowEnd = windowEnd;
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

    public Instant getWindowStart() {
        return windowStart;
    }

    public Instant getWindowEnd() {
        return windowEnd;
    }
}