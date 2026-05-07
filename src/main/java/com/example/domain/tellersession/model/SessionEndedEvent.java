package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a teller session is terminated.
 */
public record SessionEndedEvent(
    String aggregateId,
    String sessionId,
    Instant occurredAt
) implements DomainEvent {
    public SessionEndedEvent(String aggregateId) {
        this(aggregateId, UUID.randomUUID().toString(), Instant.now());
    }

    @Override
    public String type() {
        return "session.ended";
    }
}
