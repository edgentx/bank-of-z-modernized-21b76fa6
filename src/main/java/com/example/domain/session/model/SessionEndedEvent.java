package com.example.domain.session.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a teller session is terminated.
 */
public record SessionEndedEvent(
        String aggregateId,
        Instant occurredAt
) implements DomainEvent {

    public SessionEndedEvent {
        if (aggregateId == null || aggregateId.isBlank()) {
            throw new IllegalArgumentException("aggregateId cannot be null");
        }
    }

    @Override
    public String type() {
        return "session.ended";
    }

    // Factory method to match usage pattern in TellerSessionAggregate
    public static SessionEndedEvent create(String sessionId) {
        return new SessionEndedEvent(sessionId, Instant.now());
    }
}
