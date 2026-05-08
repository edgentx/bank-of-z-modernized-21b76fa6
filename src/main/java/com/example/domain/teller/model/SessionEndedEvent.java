package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Event emitted when a Teller session is terminated.
 */
public record SessionEndedEvent(
        String type,
        String aggregateId,
        Instant occurredAt
) implements DomainEvent {

    public SessionEndedEvent {
        Objects.requireNonNull(type, "type cannot be null");
        Objects.requireNonNull(aggregateId, "aggregateId cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
    }

    public static SessionEndedEvent create(String aggregateId, Instant timestamp) {
        return new SessionEndedEvent("session.ended", aggregateId, timestamp);
    }
}
