package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a Teller Session is successfully started.
 */
public record SessionStartedEvent(
        String eventId,
        String aggregateId,
        String tellerId,
        String terminalId,
        Instant occurredAt
) implements DomainEvent {
    public SessionStartedEvent {
        if (eventId == null) eventId = UUID.randomUUID().toString();
        if (occurredAt == null) occurredAt = Instant.now();
    }

    public static SessionStartedEvent create(String sessionId, String tellerId, String terminalId) {
        return new SessionStartedEvent(
                UUID.randomUUID().toString(),
                sessionId,
                tellerId,
                terminalId,
                Instant.now()
        );
    }

    @Override
    public String type() {
        return "session.started";
    }
}
