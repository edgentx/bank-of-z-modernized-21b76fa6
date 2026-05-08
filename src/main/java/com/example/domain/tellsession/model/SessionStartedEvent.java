package com.example.domain.tellsession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record SessionStartedEvent(
        String aggregateId,
        String tellerId,
        String terminalId,
        Instant occurredAt
) implements DomainEvent {
    public SessionStartedEvent {
        // Simple validation if needed, though record syntax handles it
    }

    @Override
    public String type() {
        return "session.started";
    }

    // Constructor with default timestamp for ease of use in Aggregate
    public static SessionStartedEvent create(String aggregateId, String tellerId, String terminalId) {
        return new SessionStartedEvent(aggregateId, tellerId, terminalId, Instant.now());
    }
}
