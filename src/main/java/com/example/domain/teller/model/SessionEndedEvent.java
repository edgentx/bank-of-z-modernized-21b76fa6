package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record SessionEndedEvent(
        String eventId,
        String aggregateId,
        Instant occurredAt
) implements DomainEvent {

    public SessionEndedEvent {
        Objects.requireNonNull(eventId);
        Objects.requireNonNull(aggregateId);
        Objects.requireNonNull(occurredAt);
    }

    public static SessionEndedEvent create(String aggregateId) {
        return new SessionEndedEvent(
                UUID.randomUUID().toString(),
                aggregateId,
                Instant.now()
        );
    }

    @Override
    public String type() {
        return "session.ended";
    }
}
