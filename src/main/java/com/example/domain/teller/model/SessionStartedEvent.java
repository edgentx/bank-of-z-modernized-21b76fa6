package com.example.domain.teller.model;

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
        // Basic validation
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId required");
    }

    @Override
    public String type() {
        return "session.started";
    }
}