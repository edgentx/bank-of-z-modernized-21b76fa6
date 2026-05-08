package com.example.domain.uimodel;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Domain event emitted when a Teller Session is terminated.
 * Signifies cleanup of sensitive state (auth tokens, balances, etc.).
 */
public record SessionEndedEvent(
        String aggregateId,
        String reason,
        Instant occurredAt
) implements DomainEvent {

    public SessionEndedEvent {
        Objects.requireNonNull(aggregateId, "aggregateId cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
    }

    @Override
    public String type() {
        return "session.ended";
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
