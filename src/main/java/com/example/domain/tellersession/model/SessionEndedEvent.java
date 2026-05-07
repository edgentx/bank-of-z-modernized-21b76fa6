package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a teller session is successfully terminated.
 */
public record SessionEndedEvent(
        String aggregateId,
        String sessionId,
        Instant occurredAt
) implements DomainEvent {
    public SessionEndedEvent {
        if (aggregateId == null || aggregateId.isBlank()) {
            throw new IllegalArgumentException("aggregateId cannot be null or blank");
        }
    }

    public SessionEndedEvent(String sessionId) {
        this(UUID.randomUUID().toString(), sessionId, Instant.now());
    }

    @Override
    public String type() {
        return "session.ended";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}