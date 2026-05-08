package com.example.domain.navigation.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

import java.util.UUID;

/**
 * Event emitted when a Teller Session is terminated.
 */
public class SessionEndedEvent implements DomainEvent {
    private final String eventId;
    private final String sessionId;
    private final Instant occurredAt;

    public SessionEndedEvent(String sessionId, Instant occurredAt) {
        this.eventId = UUID.randomUUID().toString();
        this.sessionId = sessionId;
        this.occurredAt = occurredAt;
    }

    @Override
    public String type() {
        return "session.ended";
    }

    @Override
    public String aggregateId() {
        return sessionId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    public String getSessionId() {
        return sessionId;
    }
}
