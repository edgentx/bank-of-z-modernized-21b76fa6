package com.example.domain.tellering.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record SessionStartedEvent(
    String aggregateId,
    String tellerId,
    String terminalId,
    String currentState,
    Instant startedAt
) implements DomainEvent {

    public SessionStartedEvent {
        if (aggregateId == null || aggregateId.isBlank()) throw new IllegalArgumentException("aggregateId required");
    }

    @Override
    public String type() {
        return "session.started";
    }

    @Override
    public Instant occurredAt() {
        return startedAt;
    }
}