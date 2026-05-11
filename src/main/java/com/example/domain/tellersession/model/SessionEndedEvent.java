package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a teller session is successfully terminated.
 * Clears sensitive state and marks the session as closed.
 */
public record SessionEndedEvent(
        String eventId,
        String aggregateId,
        String tellerId,
        Instant occurredAt
) implements DomainEvent {

    public SessionEndedEvent {
        if (aggregateId == null || aggregateId.isBlank()) {
            throw new IllegalArgumentException("aggregateId cannot be null or blank");
        }
    }

    public SessionEndedEvent(String aggregateId, String tellerId, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, tellerId, occurredAt);
    }

    @Override
    public String type() {
        return "session.ended";
    }
}
