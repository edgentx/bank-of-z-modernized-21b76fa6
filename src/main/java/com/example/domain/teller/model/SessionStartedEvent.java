package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a teller session is successfully started.
 */
public record SessionStartedEvent(
        String aggregateId,
        String tellerId,
        String terminalId,
        Instant occurredAt
) implements DomainEvent {
    public SessionStartedEvent {
        // Validate arguments
        if (aggregateId == null || aggregateId.isBlank()) throw new IllegalArgumentException("aggregateId required");
        if (tellerId == null || tellerId.isBlank()) throw new IllegalArgumentException("tellerId required");
        if (terminalId == null || terminalId.isBlank()) throw new IllegalArgumentException("terminalId required");
        if (occurredAt == null) occurredAt = Instant.now();
    }

    public SessionStartedEvent(String aggregateId, String tellerId, String terminalId) {
        this(aggregateId, tellerId, terminalId, Instant.now());
    }

    @Override
    public String type() {
        return "session.started";
    }
}