package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record SessionEndedEvent(
    String eventId,
    String aggregateId,
    Instant occurredAt
) implements DomainEvent {
    public SessionEndedEvent(String aggregateId, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, occurredAt);
    }

    @Override
    public String type() {
        return "session.ended";
    }
}
