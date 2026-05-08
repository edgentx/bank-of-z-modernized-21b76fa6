package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a teller session is successfully terminated.
 * Used in: S-20 (user-interface-navigation)
 */
public record SessionEndedEvent(
        String aggregateId,
        Instant occurredAt
) implements DomainEvent {
    public SessionEndedEvent(String aggregateId) {
        this(aggregateId, Instant.now());
    }

    @Override
    public String type() {
        return "session.ended";
    }
}
