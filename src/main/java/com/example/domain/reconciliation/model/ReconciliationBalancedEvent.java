package com.example.domain.reconciliation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Event emitted when a ReconciliationBatch is successfully balanced.
 */
public record ReconciliationBalancedEvent(
        String aggregateId,
        String operatorId,
        String justification,
        Instant occurredAt
) implements DomainEvent {

    public ReconciliationBalancedEvent {
        Objects.requireNonNull(aggregateId, "aggregateId cannot be null");
        Objects.requireNonNull(operatorId, "operatorId cannot be null");
        Objects.requireNonNull(justification, "justification cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
    }

    @Override
    public String type() {
        return "reconciliation.balanced";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}