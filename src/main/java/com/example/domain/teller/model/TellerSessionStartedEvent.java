package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record TellerSessionStartedEvent(
        String aggregateId,
        String initialMenu,
        String initialAction,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() { return "teller.session.started"; }
    @Override
    public String aggregateId() { return aggregateId; }
    @Override
    public Instant occurredAt() { return occurredAt; }
}
