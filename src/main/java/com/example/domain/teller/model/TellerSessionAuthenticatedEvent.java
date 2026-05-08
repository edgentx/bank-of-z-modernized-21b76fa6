package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

// Internal event used to setup valid state in tests
public record TellerSessionAuthenticatedEvent(
    String aggregateId,
    String tellerId,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "session.authenticated";
    }
}
