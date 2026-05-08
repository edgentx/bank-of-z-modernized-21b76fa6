package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record SessionEndedEvent(
        String aggregateId,
        Instant occurredAt
) implements DomainEvent {
    public SessionEndedEvent {
        // Validate non-null
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId required");
        if (occurredAt == null) throw new IllegalArgumentException("occurredAt required");
    }

    public SessionEndedEvent(String aggregateId) {
        this(aggregateId, Instant.now());
    }

    @Override
    public String type() {
        return "session.ended";
    }
}
