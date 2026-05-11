package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a teller session is successfully started.
 * @param sessionId The unique identifier for the session.
 * @param tellerId The authenticated ID of the teller.
 * @param terminalId The ID of the terminal where the session started.
 * @param occurredAt The timestamp when the event occurred.
 */
public record SessionStartedEvent(String sessionId, String tellerId, String terminalId, Instant occurredAt) implements DomainEvent {
    @Override
    public String type() {
        return "session.started";
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
