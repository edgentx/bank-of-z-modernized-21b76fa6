package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a teller session is successfully terminated.
 */
public record SessionEndedEvent(
        String aggregateId,
        String endedBy,
        Instant endedAt
) implements DomainEvent {

    public SessionEndedEvent(String aggregateId, String endedBy) {
        this(aggregateId, endedBy, Instant.now());
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
        return endedAt;
    }
}
