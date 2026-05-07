package com.example.domain.reconciliation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record ReconciliationStartedEvent(String batchId, String startDate, String endDate, Instant occurredAt) implements DomainEvent {
    @Override
    public String type() {
        return "reconciliation.started";
    }

    @Override
    public String aggregateId() {
        return batchId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
