package com.example.domain.reconciliation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a reconciliation batch has started.
 */
public record ReconciliationStartedEvent(
    String aggregateId,
    String batchWindow,
    Instant periodStart,
    Instant periodEnd,
    Instant occurredAt
) implements DomainEvent {

    @Override
    public String type() {
        return "reconciliation.started";
    }

    // Override method to satisfy interface requirement if field name differs (record matches automatically if names match)
}
