package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a teller session is terminated.
 * ID: S-20
 */
public record SessionEndedEvent(
        String aggregateId,
        String tellerId,
        String reason,
        Instant occurredAt
) implements DomainEvent {

    public SessionEndedEvent {
        // Basic validation
        if (aggregateId == null || aggregateId.isBlank()) throw new IllegalArgumentException("aggregateId required");
        if (occurredAt == null) occurredAt = Instant.now();
    }

    @Override
    public String type() {
        return "session.ended";
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}
