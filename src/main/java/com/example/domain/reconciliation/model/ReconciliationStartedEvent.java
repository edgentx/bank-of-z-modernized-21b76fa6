package com.example.domain.reconciliation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record ReconciliationStartedEvent(
        String event_id,
        String aggregateId,
        Instant batchWindowStart,
        Instant batchWindowEnd,
        Instant occurredAt
) implements DomainEvent {
    public ReconciliationStartedEvent(String aggregateId, Instant start, Instant end, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, start, end, occurredAt);
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
}