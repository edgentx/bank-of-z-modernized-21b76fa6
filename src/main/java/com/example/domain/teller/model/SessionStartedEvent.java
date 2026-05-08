package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a teller session is successfully initiated.
 */
public record SessionStartedEvent(
    String aggregateId,
    String tellerId,
    String terminalId,
    Instant startedAt
) implements DomainEvent {
    public SessionStartedEvent {
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId required");
        if (startedAt == null) throw new IllegalArgumentException("startedAt required");
    }

    public static SessionStartedEvent create(String sessionId, String tellerId, String terminalId) {
        return new SessionStartedEvent(sessionId, tellerId, terminalId, Instant.now());
    }

    @Override
    public String type() {
        return "session.started";
    }

    @Override
    public Instant occurredAt() {
        return startedAt;
    }
}