package com.example.domain.tellermaintenance.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Placeholder event representing the creation/start of a session.
 * Used to simulate state in S-20 steps for successful scenario context.
 */
public record TellerSessionStartedEvent(
        String aggregateId,
        String tellerId,
        String terminalId,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() { return "session.started"; }

    @Override
    public String aggregateId() { return aggregateId; }

    @Override
    public Instant occurredAt() { return occurredAt; }
}
