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
        // Ensure non-null for record validity
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId cannot be null");
    }

    public SessionEndedEvent(String aggregateId, String sessionId, String tellerId) {
        this(aggregateId, sessionId, tellerId, Instant.now());
    }

    @Override
    public String type() {
        return "session.ended";
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}