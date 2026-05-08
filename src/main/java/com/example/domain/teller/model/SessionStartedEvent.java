package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record SessionStartedEvent(String aggregateId, String tellerId, String terminalId, Instant occurredAt) implements DomainEvent {
    public SessionStartedEvent {
        // Validations at construction time if necessary
    }

    @Override
    public String type() {
        return "session.started";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    // Static factory method to ensure consistent ID generation if needed, though usually handled by aggregate
    public static SessionStartedEvent create(String sessionId, String tellerId, String terminalId) {
        return new SessionStartedEvent(sessionId, tellerId, terminalId, Instant.now());
    }
}
