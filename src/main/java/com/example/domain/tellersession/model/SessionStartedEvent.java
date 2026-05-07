package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a teller session is successfully started.
 */
public record SessionStartedEvent(
        String aggregateId,
        String tellerId,
        String terminalId,
        String operationalContext,
        Instant occurredAt
) implements DomainEvent {
    public SessionStartedEvent {
        // Validate basic invariants for the event structure
        if (aggregateId == null || aggregateId.isBlank()) {
            throw new IllegalArgumentException("aggregateId cannot be null");
        }
    }

    @Override
    public String type() {
        return "session.started";
    }
}