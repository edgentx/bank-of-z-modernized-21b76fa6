package com.example.domain.reconciliation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

public record ReconciliationBalancedEvent(String batchId, String operatorId, String justification, Instant occurredAt) implements DomainEvent {
    public ReconciliationBalancedEvent {
        Objects.requireNonNull(batchId, "batchId cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
    }

    @Override
    public String type() {
        return "reconciliation.balanced";
    }

    @Override
    public String aggregateId() {
        return batchId;
    }
}