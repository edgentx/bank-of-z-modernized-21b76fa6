package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a session start command is rejected due to invariant violations.
 * @param sessionId The unique identifier for the session.
 * @param reason The domain error reason for rejection.
 * @param occurredAt The timestamp when the event occurred.
 */
public record SessionRejectedEvent(String sessionId, String reason, Instant occurredAt) implements DomainEvent {
    @Override
    public String type() {
        return "session.rejected";
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
