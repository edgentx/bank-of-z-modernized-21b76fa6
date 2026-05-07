package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a teller session is successfully terminated.
 */
public record SessionEndedEvent(
        String aggregateId,
        String sessionId,
        Instant occurredAt
) implements DomainEvent {
    public SessionEndedEvent(String aggregateId, String sessionId) {
        this(aggregateId, sessionId, Instant.now());
    }

    @Override
    public String type() {
        return "session.ended";
    }
}