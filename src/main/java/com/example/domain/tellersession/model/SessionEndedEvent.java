package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain event published when a teller session is successfully terminated.
 * Contains the session ID and the timestamp of termination.
 */
public record SessionEndedEvent(UUID sessionId, Instant occurredAt) implements DomainEvent {
    @Override
    public String type() {
        return "session.ended";
    }

    @Override
    public String aggregateId() {
        return sessionId != null ? sessionId.toString() : null;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
