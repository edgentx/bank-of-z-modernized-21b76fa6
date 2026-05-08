package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record SessionStartedEvent(
    String aggregateId,
    String tellerId,
    String terminalId,
    Instant occurredAt
) implements DomainEvent {
    public SessionStartedEvent {
        // Validation in constructor if needed, though records are strict by default
    }

    @Override
    public String type() {
        return "session.started";
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    // Factory method for cleaner creation
    public static SessionStartedEvent create(String sessionId, String tellerId, String terminalId) {
        return new SessionStartedEvent(sessionId, tellerId, terminalId, Instant.now());
    }
}