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
        // Ensure defaults if null, though aggregate should supply valid IDs
        if (occurredAt == null) occurredAt = Instant.now();
    }

    @Override
    public String type() {
        return "session.started";
    }
}