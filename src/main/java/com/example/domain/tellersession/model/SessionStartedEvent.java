package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a Teller Session is successfully started.
 */
public record SessionStartedEvent(
    String sessionId,
    String tellerId,
    String terminalId,
    Instant occurredAt,
    String eventType // Required by DomainEvent interface
) implements DomainEvent {

    public SessionStartedEvent(String sessionId, String tellerId, String terminalId, Instant occurredAt) {
        this(sessionId, tellerId, terminalId, occurredAt, "SessionStartedEvent");
    }

    @Override
    public String type() {
        return eventType;
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
