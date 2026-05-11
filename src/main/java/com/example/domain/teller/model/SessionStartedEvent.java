package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a teller session is successfully started.
 * Contains the context necessary for downstream projections to initialize the session state.
 */
public record SessionStartedEvent(
        String eventId,
        String aggregateId,
        String type,
        String sessionId,
        String tellerId,
        String terminalId,
        Instant occurredAt
) implements DomainEvent {

    public SessionStartedEvent {
        if (eventId == null || eventId.isBlank()) {
            eventId = UUID.randomUUID().toString();
        }
        if (type == null || type.isBlank()) {
            type = "session.started";
        }
        if (occurredAt == null) {
            occurredAt = Instant.now();
        }
    }

    // Factory method for easier creation within the aggregate
    public static SessionStartedEvent create(String sessionId, String tellerId, String terminalId, Instant occurredAt) {
        return new SessionStartedEvent(
                UUID.randomUUID().toString(),
                sessionId,
                "session.started",
                sessionId,
                tellerId,
                terminalId,
                occurredAt
        );
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
