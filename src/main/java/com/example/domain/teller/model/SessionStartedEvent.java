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
        // Validate inputs
        if (aggregateId == null || aggregateId.isBlank()) throw new IllegalArgumentException("aggregateId cannot be null");
        if (tellerId == null || tellerId.isBlank()) throw new IllegalArgumentException("tellerId cannot be null");
        if (terminalId == null || terminalId.isBlank()) throw new IllegalArgumentException("terminalId cannot be null");
        if (occurredAt == null) throw new IllegalArgumentException("occurredAt cannot be null");
    }

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

    public static SessionStartedEvent create(String aggregateId, String tellerId, String terminalId) {
        return new SessionStartedEvent(aggregateId, tellerId, terminalId, Instant.now());
    }
}
