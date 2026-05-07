package com.example.domain.reconciliation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a reconciliation batch has successfully started.
 */
public record ReconciliationStartedEvent(
        String aggregateId,
        Instant batchWindowStart,
        Instant batchWindowEnd,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "reconciliation.started";
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
