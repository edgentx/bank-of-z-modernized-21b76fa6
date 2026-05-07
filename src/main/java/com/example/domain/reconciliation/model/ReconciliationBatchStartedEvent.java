package com.example.domain.reconciliation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class ReconciliationBatchStartedEvent implements DomainEvent {
    private final String eventId = UUID.randomUUID().toString();
    private final String aggregateId;
    private final String type = "reconciliation.started";
    private final Instant occurredAt;
    private final Instant batchWindowStart;
    private final Instant batchWindowEnd;

    public ReconciliationBatchStartedEvent(String aggregateId, Instant batchWindowStart, Instant batchWindowEnd, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.batchWindowStart = batchWindowStart;
        this.batchWindowEnd = batchWindowEnd;
        this.occurredAt = occurredAt;
    }

    @Override
    public String type() {
        return type;
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

    public String getEventId() {
        return eventId;
    }
}
