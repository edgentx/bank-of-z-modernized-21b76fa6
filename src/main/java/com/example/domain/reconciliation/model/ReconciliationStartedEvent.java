package com.example.domain.reconciliation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

public class ReconciliationStartedEvent implements DomainEvent {
    private final String aggregateId;
    private final Instant batchWindow;
    private final Instant occurredAt;

    public ReconciliationStartedEvent(String aggregateId, Instant batchWindow, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.batchWindow = batchWindow;
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

    public Instant getBatchWindow() {
        return batchWindow;
    }
}
