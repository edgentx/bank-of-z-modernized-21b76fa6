package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event representing the initialization of a Teller Session.
 * Used to hydrate the aggregate for tests and business flow.
 */
public record TellerSessionLoggedInEvent(
    String aggregateId,
    String tellerId,
    String initialMenu,
    Instant occurredAt
) implements DomainEvent {
    public TellerSessionLoggedInEvent {
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId required");
    }

    @Override
    public String type() {
        return "teller.session.logged_in";
    }
}
