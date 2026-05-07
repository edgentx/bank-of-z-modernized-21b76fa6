package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record SessionEndedEvent(
        String aggregateId,
        String sessionId,
        String tellerId,
        Instant occurredAt
) implements DomainEvent {
    public SessionEndedEvent {
        // Validate required fields
        if (aggregateId == null || aggregateId.isBlank()) {
            throw new IllegalArgumentException("Aggregate ID cannot be blank");
        }
    }

    @Override
    public String type() {
        return "session.ended";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    // Factory method to simplify aggregate creation
    public static SessionEndedEvent create(String aggregateId, String sessionId, String tellerId) {
        return new SessionEndedEvent(aggregateId, sessionId, tellerId, Instant.now());
    }
}
