package com.example.domain.teller.model;

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
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId required");
        if (occurredAt == null) throw new IllegalArgumentException("occurredAt required");
    }

    @Override
    public String type() {
        return "session.ended";
    }

    // Helper for Cucumber assertions to simplify equals checks if we generate IDs in the test
    public static SessionEndedEvent create(String aggregateId, Instant occurredAt) {
        return new SessionEndedEvent(aggregateId, occurredAt);
    }
}
