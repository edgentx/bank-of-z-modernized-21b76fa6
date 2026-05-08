package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a teller session is terminated.
 * S-20: EndSessionCmd.
 */
public record SessionEndedEvent(
        String eventId,
        String aggregateId,
        String sessionId,
        Instant occurredAt
) implements DomainEvent {

    public SessionEndedEvent {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("sessionId cannot be null or blank");
        }
        if (occurredAt == null) {
            throw new IllegalArgumentException("occurredAt cannot be null");
        }
    }

    public SessionEndedEvent(String sessionId, Instant occurredAt) {
        this(UUID.randomUUID().toString(), sessionId, sessionId, occurredAt);
    }

    @Override
    public String type() {
        return "session.ended";
    }
}