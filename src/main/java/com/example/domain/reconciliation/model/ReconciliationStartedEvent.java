package com.example.domain.reconciliation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a reconciliation batch is started.
 */
public class ReconciliationStartedEvent implements DomainEvent {

    private final String eventId = UUID.randomUUID().toString();
    private final String aggregateId;
    private final String batchWindow;
    private final Instant occurredAt;

    public ReconciliationStartedEvent(String aggregateId, String batchWindow, Instant occurredAt) {
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

    public String getBatchWindow() {
        return batchWindow;
    }

    public String getEventId() {
        return eventId;
    }
}