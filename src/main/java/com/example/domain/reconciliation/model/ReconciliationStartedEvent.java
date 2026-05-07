package com.example.domain.reconciliation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record ReconciliationStartedEvent(
        String aggregateId,
        Instant batchStart,
        Instant batchEnd,
        Instant occurredAt
) implements DomainEvent {

    @Override
    public String type() {
        return "ReconciliationStarted";
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
