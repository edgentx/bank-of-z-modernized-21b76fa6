package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record SessionEndedEvent(
    String aggregateId,
    Instant occurredAt
) implements DomainEvent {
    public SessionEndedEvent {
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId required");
    }

    @Override
    public String type() {
        return "session.ended";
    }

    // Canonical constructor for use in Aggregate
    public static SessionEndedEvent create(String aggregateId, Instant occurredAt) {
        return new SessionEndedEvent(aggregateId, occurredAt);
    }
}
