package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event representing the start of a session.
 * (Required to support the "valid TellerSession aggregate" context in tests)
 */
public record SessionStartedEvent(
        String aggregateId,
        String tellerId,
        String tillId,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "session.started";
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
