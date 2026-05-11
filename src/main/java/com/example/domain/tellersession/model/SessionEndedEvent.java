package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a teller session is successfully terminated.
 */
public record SessionEndedEvent(
        String aggregateId,
        Instant occurredAt
) implements DomainEvent {

    public SessionEndedEvent {
        // Ensure basic immutability/validation if needed, though records handle most.
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId cannot be null");
    }

    @Override
    public String type() {
        return "session.ended";
    }
}