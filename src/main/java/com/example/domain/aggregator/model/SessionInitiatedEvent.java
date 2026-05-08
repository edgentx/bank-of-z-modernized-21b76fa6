package com.example.domain.aggregator.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event representing the successful start of a teller session.
 * Placed here to allow hydration of the aggregate for EndSessionCmd tests.
 */
public record SessionInitiatedEvent(String aggregateId, String tellerId, Instant occurredAt) implements DomainEvent {
    @Override
    public String type() {
        return "session.initiated";
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
