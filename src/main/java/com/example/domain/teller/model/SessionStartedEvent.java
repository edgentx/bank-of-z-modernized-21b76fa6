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
        // Ensure defaults if null, though constructor should provide valid data
        if (occurredAt == null) occurredAt = Instant.now();
    }

    @Override
    public String type() {
        return "session.started";
    }
}