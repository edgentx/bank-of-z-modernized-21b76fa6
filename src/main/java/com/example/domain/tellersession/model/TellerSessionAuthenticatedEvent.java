package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event representing the successful authentication of a teller.
 * Used to establish the session context required by S-19 invariants.
 */
public record TellerSessionAuthenticatedEvent(String aggregateId, String tellerId, Instant occurredAt) implements DomainEvent {
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
        return occurredAt;
    }
}
