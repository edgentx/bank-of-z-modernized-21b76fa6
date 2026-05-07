package com.example.domain.reconciliation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a reconciliation batch process is started.
 */
public record ReconciliationStartedEvent(
        String aggregateId,
        Instant windowStart,
        Instant windowEnd,
        Instant occurredAt
) implements DomainEvent {

    public ReconciliationStartedEvent {
        if (aggregateId == null || aggregateId.isBlank()) {
            throw new IllegalArgumentException("aggregateId cannot be null");
        }
        if (occurredAt == null) {
            occurredAt = Instant.now();
        }
    }

    @Override
    public String type() {
        return "reconciliation.started";
    }
}
