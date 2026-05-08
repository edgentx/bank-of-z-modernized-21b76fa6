package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a TellerSession is successfully terminated.
 * Contains the timestamp of termination for audit purposes.
 */
public record SessionEndedEvent(
        String aggregateId,
        Instant occurredAt
) implements DomainEvent {

    public SessionEndedEvent {
        // Defensive copy/validation if necessary
        if (aggregateId == null || aggregateId.isBlank()) {
            throw new IllegalArgumentException("aggregateId cannot be null");
        }
    }

    public static SessionEndedEvent create(String aggregateId) {
        return new SessionEndedEvent(aggregateId, Instant.now());
    }

    @Override
    public String type() {
        return "session.ended";
    }
}
