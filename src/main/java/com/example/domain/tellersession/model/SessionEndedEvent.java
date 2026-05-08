package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a Teller Session is successfully terminated.
 */
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

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
