package com.example.domain.uimodel.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Internal event indicating session expiration logic violation check.
 */
public record SessionExpiredEvent(
    String sessionId,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "session.expired";
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
