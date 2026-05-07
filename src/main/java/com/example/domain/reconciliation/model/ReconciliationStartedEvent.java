package com.example.domain.reconciliation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record ReconciliationStartedEvent(
        String eventId,
        String aggregateId,
        Instant start,
        Instant end,
        Instant occurredAt
) implements DomainEvent {
    public ReconciliationStartedEvent {
        // Defensive validation
        if (eventId == null) eventId = UUID.randomUUID().toString();
    }

    public ReconciliationStartedEvent(String aggregateId, Instant start, Instant end, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, start, end, occurredAt);
    }

    @Override
    public String type() {
        return "reconciliation.started";
    }
}