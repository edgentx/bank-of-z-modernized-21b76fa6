package com.example.domain.reconciliation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record ReconciliationBalancedEvent(
        String aggregateId,
        String operatorId,
        String justification,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "reconciliation.balanced";
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
