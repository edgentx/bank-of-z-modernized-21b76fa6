package com.example.domain.reconciliation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a reconciliation batch is started.
 * Corresponds to Story S-16.
 */
public class ReconciliationStartedEvent implements DomainEvent {

    private final String eventId = UUID.randomUUID().toString();
    private final String aggregateId;
    private final String batchId;
    private final Instant windowStart;
    private final Instant windowEnd;
    private final String operatorId;
    private final Instant occurredAt;

    public ReconciliationStartedEvent(String batchId, Instant windowStart, Instant windowEnd, String operatorId, Instant occurredAt) {
        this.batchId = batchId;
        this.aggregateId = batchId;
        this.windowStart = windowStart;
        this.windowEnd = windowEnd;
        this.operatorId = operatorId;
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

    public String batchId() {
        return batchId;
    }

    public Instant windowStart() {
        return windowStart;
    }

    public Instant windowEnd() {
        return windowEnd;
    }

    public String operatorId() {
        return operatorId;
    }
}