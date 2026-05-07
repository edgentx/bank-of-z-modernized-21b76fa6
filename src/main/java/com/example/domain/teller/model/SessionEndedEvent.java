package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a teller session is ended.
 * Part of S-20: EndSessionCmd on TellerSession.
 */
public record SessionEndedEvent(
        String eventId,
        String aggregateId,
        Instant occurredAt
) implements DomainEvent {
    public SessionEndedEvent {
        if (eventId == null || eventId.isBlank()) {
            eventId = UUID.randomUUID().toString();
        }
        if (occurredAt == null) {
            occurredAt = Instant.now();
        }
    }

    public SessionEndedEvent(String aggregateId) {
        this(UUID.randomUUID().toString(), aggregateId, Instant.now());
    }

    @Override
    public String type() {
        return "session.ended";
    }
}
