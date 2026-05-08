package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a teller session is started.
 * S-18
 */
public record SessionStartedEvent(
    String aggregateId,
    String tellerId,
    String terminalId,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "session.started";
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}
