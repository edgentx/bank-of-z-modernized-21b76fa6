package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a teller session is started.
 */
public record SessionStartedEvent(
    String sessionId,
    String tellerId,
    String terminalId,
    Instant startedAt
) implements DomainEvent {
    @Override
    public String type() {
        return "teller.session.started";
    }

    @Override
    public String aggregateId() {
        return sessionId;
    }

    @Override
    public Instant occurredAt() {
        return startedAt;
    }
}
