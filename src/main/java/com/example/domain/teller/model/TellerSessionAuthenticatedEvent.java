package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record TellerSessionAuthenticatedEvent(
        String aggregateId,
        String tellerId,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() { return "teller.session.authenticated"; }
    @Override
    public String aggregateId() { return aggregateId; }
    @Override
    public Instant occurredAt() { return occurredAt; }
}
