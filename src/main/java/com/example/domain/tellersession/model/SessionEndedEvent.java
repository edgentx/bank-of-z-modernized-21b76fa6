package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a teller session is ended.
 */
public record SessionEndedEvent(
    UUID aggregateId,
    String type,
    Instant occurredAt
) implements DomainEvent {

    public SessionEndedEvent(UUID aggregateId, Instant occurredAt) {
        this(aggregateId, "session.ended", occurredAt);
    }

    @Override
    public String aggregateId() {
        return aggregateId.toString();
    }
}
