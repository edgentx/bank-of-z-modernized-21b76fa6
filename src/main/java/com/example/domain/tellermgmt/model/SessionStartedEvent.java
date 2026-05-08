package com.example.domain.tellermgmt.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a Teller successfully starts a session.
 */
public record SessionStartedEvent(
        String aggregateId,
        String tellerId,
        String terminalId,
        Instant occurredAt
) implements DomainEvent {

    public SessionStartedEvent {
        // Defensive validation
        if (aggregateId == null || aggregateId.isBlank()) {
            throw new IllegalArgumentException("aggregateId required");
        }
    }

    @Override
    public String type() {
        return "SessionStartedEvent";
    }

    // Factory method to ensure consistent IDs if needed, though constructor usually suffices
    public static SessionStartedEvent create(String sessionId, String tellerId, String terminalId) {
        return new SessionStartedEvent(sessionId, tellerId, terminalId, Instant.now());
    }
}
