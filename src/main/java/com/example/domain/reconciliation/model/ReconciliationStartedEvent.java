package com.example.domain.reconciliation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record ReconciliationStartedEvent(
        String aggregateId,
        Instant batchWindow,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "reconciliation.started";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}
