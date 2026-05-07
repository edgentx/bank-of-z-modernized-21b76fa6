package com.example.domain.reconciliation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Event emitted when a reconciliation batch is successfully started.
 */
public record ReconciliationStartedEvent(
        String batchId,
        Instant windowStart,
        Instant windowEnd,
        Instant occurredAt
) implements DomainEvent {
    public ReconciliationStartedEvent {
        Objects.requireNonNull(batchId, "batchId is required");
        Objects.requireNonNull(windowStart, "windowStart is required");
        Objects.requireNonNull(windowEnd, "windowEnd is required");
        Objects.requireNonNull(occurredAt, "occurredAt is required");
    }

    @Override
    public String type() {
        return "reconciliation.started";
    }

    @Override
    public String aggregateId() {
        return batchId;
    }
}