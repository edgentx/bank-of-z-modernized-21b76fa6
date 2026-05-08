package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a Teller Session is successfully terminated.
 */
public record SessionEndedEvent(
        String aggregateId,
        String type,
        Instant occurredAt
) implements DomainEvent {

    public SessionEndedEvent(String aggregateId, Instant occurredAt) {
        this(aggregateId, "session.ended", occurredAt);
    }

    @Override
    public String type() {
        return type;
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
