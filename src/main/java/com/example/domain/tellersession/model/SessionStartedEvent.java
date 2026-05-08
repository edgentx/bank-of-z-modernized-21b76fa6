package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a teller session is successfully started.
 */
public record SessionStartedEvent(
    String aggregateId,
    String tellerId,
    String terminalId,
    Instant occurredAt
) implements DomainEvent {

    public SessionStartedEvent {
        // Ensure aggregateId is never null, fallback to random UUID if strictly needed, though usually set by aggregate.
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId cannot be null");
        if (occurredAt == null) occurredAt = Instant.now();
    }

    @Override
    public String type() {
        return "session.started";
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
