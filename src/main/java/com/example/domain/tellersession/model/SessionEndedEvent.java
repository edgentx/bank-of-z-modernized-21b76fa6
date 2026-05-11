package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a teller session is successfully terminated.
 * Contains the audit trail for the closure.
 */
public record SessionEndedEvent(
        String type,
        String aggregateId,
        Instant occurredAt,
        String sessionId
) implements DomainEvent {

    public SessionEndedEvent(String aggregateId) {
        this("session.ended", aggregateId, Instant.now(), aggregateId);
    }

    @Override
    public String type() {
        return type;
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
