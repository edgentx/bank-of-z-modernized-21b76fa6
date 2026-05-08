package com.example.domain.tellermenu.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event representing the authentication of a teller session.
 * Used by tests to hydrate the aggregate state.
 */
public record TellerSessionAuthenticatedEvent(
        String aggregateId,
        String tellerId,
        Instant loggedInAt
) implements DomainEvent {
    @Override
    public String type() {
        return "teller.authenticated";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return loggedInAt;
    }
}
