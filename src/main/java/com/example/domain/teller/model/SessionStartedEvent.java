package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a teller session is successfully started.
 */
public record SessionStartedEvent(
        String aggregateId,
        String tellerId,
        String terminalId,
        Instant startedAt
) implements DomainEvent {
    public SessionStartedEvent {
        if (aggregateId == null) aggregateId = UUID.randomUUID().toString();
        if (startedAt == null) startedAt = Instant.now();
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
