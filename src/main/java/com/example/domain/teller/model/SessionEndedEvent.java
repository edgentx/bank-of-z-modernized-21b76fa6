package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record SessionEndedEvent(
        String aggregateId,
        String sessionId,
        Instant endedAt
) implements DomainEvent {
    public SessionEndedEvent {
        // Validation logic if necessary
    }

    public SessionEndedEvent(String sessionId, Instant endedAt) {
        this(UUID.randomUUID().toString(), sessionId, endedAt);
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
        return endedAt;
    }
}
