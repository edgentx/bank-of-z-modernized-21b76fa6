package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event representing the start of a teller session.
 * Used to hydrate the aggregate for testing invariants like auth and timeout.
 */
public record SessionStartedEvent(String aggregateId, String tellerId, Instant occurredAt) implements DomainEvent {
    @Override
    public String type() {
        return "session.started";
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
