package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Event representing the opening of a teller session.
 * Used here primarily for testing setup to simulate a valid existing state.
 */
public record TellerSessionOpenedEvent(String aggregateId, String tellerId, Instant occurredAt) implements DomainEvent {
    public TellerSessionOpenedEvent {
        Objects.requireNonNull(aggregateId);
        Objects.requireNonNull(occurredAt);
    }

    @Override
    public String type() {
        return "session.opened";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}
