package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a teller session is terminated.
 * Clears sensitive state from the Teller terminal context.
 */
public record SessionEndedEvent(
        String aggregateId,
        String tellerId,
        Instant occurredAt
) implements DomainEvent {
    public SessionEndedEvent(String aggregateId, String tellerId) {
        this(aggregateId, tellerId, Instant.now());
    }

    @Override
    public String type() {
        return "session.ended";
    }
}
