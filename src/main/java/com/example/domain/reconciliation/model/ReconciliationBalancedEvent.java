package com.example.domain.reconciliation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a Reconciliation Batch is forced to a balanced state.
 */
public record ReconciliationBalancedEvent(
        String eventId,
        String aggregateId,
        String operatorId,
        String justification,
        Instant occurredAt
) implements DomainEvent {

    public ReconciliationBalancedEvent(String aggregateId, String operatorId, String justification, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, operatorId, justification, occurredAt);
    }

    @Override
    public String type() {
        return "reconciliation.balanced";
    }
}
