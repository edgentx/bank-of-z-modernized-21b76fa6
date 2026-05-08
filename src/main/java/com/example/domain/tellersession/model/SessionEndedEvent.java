package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event published when a teller session is successfully terminated.
 * Sensitive state should be cleared upon handling this event.
 */
public record SessionEndedEvent(
        String aggregateId,
        Instant occurredAt
) implements DomainEvent {
    public SessionEndedEvent {
        // Defensive validation in record constructor
        if (aggregateId == null || aggregateId.isBlank()) {
            throw new IllegalArgumentException("aggregateId cannot be blank");
        }
    }

    public static SessionEndedEvent create(String aggregateId) {
        return new SessionEndedEvent(aggregateId, Instant.now());
    }

    @Override
    public String type() {
        return "session.ended";
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}
