package com.example.domain.reconciliation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record ReconciliationStartedEvent(
        String aggregateId,
        String eventId,
        String batchWindow,
        Instant occurredAt
) implements DomainEvent {
    public ReconciliationStartedEvent(String aggregateId, String batchWindow, Instant occurredAt) {
        this(
                aggregateId,
                UUID.randomUUID().toString(),
                batchWindow,
                occurredAt
        );
    }

    @Override
    public String type() {
        return "ReconciliationStarted";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}
