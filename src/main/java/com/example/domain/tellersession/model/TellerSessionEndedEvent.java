package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Event emitted when a teller session is terminated.
 * Context: Teller Session (S-20).
 */
public record TellerSessionEndedEvent(String sessionId, Instant occurredAt) implements DomainEvent {
    public TellerSessionEndedEvent {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
    }

    @Override
    public String type() {
        return "session.ended";
    }

    @Override
    public String aggregateId() {
        return sessionId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
