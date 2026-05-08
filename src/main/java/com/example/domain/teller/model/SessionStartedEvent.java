package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record SessionStartedEvent(
    String sessionId,
    String tellerId,
    String terminalId,
    Instant occurredAt
) implements DomainEvent {
    public SessionStartedEvent {
        // Validate eventId is not strictly required by the contract, but good practice
    }
    @Override
    public String type() {
        return "session.started";
    }

    @Override
    public String aggregateId() {
        return sessionId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
