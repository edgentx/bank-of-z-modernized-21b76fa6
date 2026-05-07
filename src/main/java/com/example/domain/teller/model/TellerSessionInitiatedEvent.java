package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record TellerSessionInitiatedEvent(String aggregateId, String tellerId, String terminalId, Instant occurredAt) implements DomainEvent {
    @Override
    public String type() {
        return "teller.session.initiated";
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
