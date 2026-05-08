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
        // Ensure aggregateId is present
        if (aggregateId == null || aggregateId.isBlank()) {
            throw new IllegalArgumentException("aggregateId cannot be null");
        }
    }

    public SessionStartedEvent(String sessionId, String tellerId, String terminalId) {
        this(sessionId, tellerId, terminalId, Instant.now());
    }

    @Override
    public String type() {
        return "teller.session.started";
    }
}
