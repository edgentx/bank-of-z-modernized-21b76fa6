package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a teller session is successfully ended.
 */
public record SessionEndedEvent(
    String aggregateId,
    String type,
    Instant occurredAt
) implements DomainEvent {

    public SessionEndedEvent(String aggregateId) {
        this(aggregateId, "session.ended", Instant.now());
    }
}
