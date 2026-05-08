package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record SessionEndedEvent(
    String aggregateId,
    String sessionId,
    Instant occurredAt
) implements DomainEvent {
    public SessionEndedEvent {
        // Validate immutable state
        if (aggregateId == null || aggregateId.isBlank()) throw new IllegalArgumentException("aggregateId required");
        if (sessionId == null || sessionId.isBlank()) throw new IllegalArgumentException("sessionId required");
        if (occurredAt == null) throw new IllegalArgumentException("occurredAt required");
    }

    public SessionEndedEvent(String sessionId) {
        this(UUID.randomUUID().toString(), sessionId, Instant.now());
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
}
