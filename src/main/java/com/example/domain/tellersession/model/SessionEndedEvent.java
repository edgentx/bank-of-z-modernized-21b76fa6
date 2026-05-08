package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a teller session is terminated.
 * S-20: User-Interface-Navigation
 */
public record SessionEndedEvent(
        String eventId,
        String aggregateId,
        String sessionId,
        Instant occurredAt
) implements DomainEvent {
    public SessionEndedEvent(String aggregateId, String sessionId, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, sessionId, occurredAt);
    }

    @Override
    public String type() {
        return "session.ended";
    }
}