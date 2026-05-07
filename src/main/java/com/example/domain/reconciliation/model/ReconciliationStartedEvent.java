package com.example.domain.reconciliation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a reconciliation batch has started processing.
 */
public record ReconciliationStartedEvent(
        String aggregateId,
        Instant windowStart,
        Instant windowEnd,
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