package com.example.domain.reconciliation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Domain event emitted when a reconciliation batch is started.
 * @param aggregateId The ID of the batch.
 * @param batchWindow The date window.
 * @param occurredAt When the event happened.
 */
public record ReconciliationStartedEvent(
    String aggregateId,
    LocalDate batchWindow,
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

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
