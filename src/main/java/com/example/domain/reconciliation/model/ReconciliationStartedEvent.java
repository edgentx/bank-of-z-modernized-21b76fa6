package com.example.domain.reconciliation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a reconciliation batch is started.
 */
public record ReconciliationStartedEvent(
        String aggregateId,
        Instant batchStart,
        Instant batchEnd,
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
