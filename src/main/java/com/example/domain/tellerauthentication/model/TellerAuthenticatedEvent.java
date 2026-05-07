package com.example.domain.tellerauthentication.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a teller successfully authenticates.
 * Used by TellerSession to allow StartSessionCmd.
 */
public record TellerAuthenticatedEvent(
        String aggregateId,
        String tellerId,
        Instant occurredAt
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
        return occurredAt;
    }
}
