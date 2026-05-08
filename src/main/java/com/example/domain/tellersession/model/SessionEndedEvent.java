package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

import java.util.UUID;

/**
 * Event emitted when a Teller Session is successfully terminated.
 */
public record SessionEndedEvent(
    String aggregateId,
    Instant occurredAt
) implements DomainEvent {

    public SessionEndedEvent {
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId required");
        if (occurredAt == null) throw new IllegalArgumentException("occurredAt required");
    }

    @Override
    public String type() {
        return "session.ended";
    }
}
