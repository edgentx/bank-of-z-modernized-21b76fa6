package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a teller session is successfully started.
 * S-18: Domain Event.
 */
public record SessionStartedEvent(
    String aggregateId,
    String tellerId,
    String terminalId,
    Instant occurredAt
) implements DomainEvent {

    @Override
    public String type() {
        return "teller.session.started";
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
