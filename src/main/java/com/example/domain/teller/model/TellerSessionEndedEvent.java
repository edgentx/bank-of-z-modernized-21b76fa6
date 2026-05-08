package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a teller session is successfully terminated.
 */
public record TellerSessionEndedEvent(
        String eventId,
        String aggregateId,
        Instant occurredAt
) implements DomainEvent {

    public TellerSessionEndedEvent {
        // Defensive validation for invariants
        if (aggregateId == null || aggregateId.isBlank()) {
            throw new IllegalArgumentException("aggregateId cannot be null or blank");
        }
    }

    public TellerSessionEndedEvent(String aggregateId, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, occurredAt);
    }

    @Override
    public String type() {
        return "teller.session.ended";
    }
}
