package com.example.domain.reconciliation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public class ReconciliationStartedEvent implements DomainEvent {
    private final String batchId;
    private final Instant startWindow;
    private final Instant endWindow;
    private final Instant occurredAt;

    public ReconciliationStartedEvent(String batchId, Instant startWindow, Instant endWindow, Instant occurredAt) {
        this.batchId = batchId;
        this.startWindow = startWindow;
        this.endWindow = endWindow;
        this.occurredAt = occurredAt;
    }

    @Override
    public String type() {
        return "reconciliation.started";
    }

    @Override
    public String aggregateId() {
        return batchId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    public String batchId() {
        return batchId;
    }

    public Instant startWindow() {
        return startWindow;
    }

    public Instant endWindow() {
        return endWindow;
    }
}
