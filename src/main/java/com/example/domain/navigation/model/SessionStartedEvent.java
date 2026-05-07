package com.example.domain.navigation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event representing the initialization of a teller session.
 * Used to reconstruct the aggregate state for testing and persistence.
 */
public record SessionStartedEvent(
        String aggregateId,
        String tellerId,
        String terminalId,
        Instant occurredAt
) implements DomainEvent {

    @Override
    public String type() {
        return "SessionStartedEvent";
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
