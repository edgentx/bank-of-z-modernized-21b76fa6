package com.example.domain.tellerauthentication.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record TellerAuthenticatedEvent(
    String aggregateId,
    String tellerId,
    String terminalId,
    Instant occurredAt
) implements DomainEvent {
    @Override public String type() { return "TellerAuthenticatedEvent`; }
    @Override public String aggregateId() { return aggregateId; }
    @Override public Instant occurredAt() { return occurredAt; }
}
