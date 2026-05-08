package com.example.domain.tellersession.model;

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
        if (aggregateId == null || aggregateId.isBlank()) throw new IllegalArgumentException("aggregateId required");
        if (occurredAt == null) occurredAt = Instant.now();
    }

    @Override
    public String type() {
        return "session.started";
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    // Helper to simulate a static factory or avoid constructor ambiguity if necessary,
    // though the record constructor is sufficient.
    public static SessionStartedEvent create(String sessionId, String tellerId, String terminalId, Instant time) {
        return new SessionStartedEvent(sessionId, tellerId, terminalId, time);
    }
}
