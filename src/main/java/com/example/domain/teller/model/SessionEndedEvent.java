package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a teller session is successfully terminated.
 */
public record SessionEndedEvent(
        String aggregateId,
        String tellerId,
        Instant endedAt
) implements DomainEvent {
    @Override
    public String type() {
        return "session.ended";
    }

    @Override
    public Instant occurredAt() {
        return endedAt;
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}
