package com.example.domain.reconciliation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a reconciliation batch is started.
 */
public class ReconciliationStartedEvent implements DomainEvent {
    private final String aggregateId;
    private final Instant startWindow;
    private final Instant endWindow;
    private final Instant occurredAt;

    public ReconciliationStartedEvent(String aggregateId, Instant startWindow, Instant endWindow, Instant occurredAt) {
        this.aggregateId = aggregateId;
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
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    public Instant startWindow() {
        return startWindow;
    }

    public Instant endWindow() {
        return endWindow;
    }
}
