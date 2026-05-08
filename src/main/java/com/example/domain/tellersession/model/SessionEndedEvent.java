package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a teller session is successfully terminated.
 */
public record SessionEndedEvent(
    String aggregateId,
    String sessionId,
    Instant occurredAt
) implements DomainEvent {
    public SessionEndedEvent(String sessionId, Instant occurredAt) {
        this(UUID.randomUUID().toString(), sessionId, occurredAt);
    }

    @Override
    public String type() {
        return "session.ended";
    }

    @Override
    public String aggregateId() {
        return aggregateId();
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
