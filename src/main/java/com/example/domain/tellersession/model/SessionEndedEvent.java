package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record SessionEndedEvent(
        String eventId,
        String aggregateId,
        String type,
        Instant occurredAt
) implements DomainEvent {

    public SessionEndedEvent {
        if (eventId == null || eventId.isBlank()) {
            eventId = UUID.randomUUID().toString();
        }
        if (type == null) {
            type = "session.ended";
        }
        if (occurredAt == null) {
            occurredAt = Instant.now();
        }
    }

    public SessionEndedEvent(String aggregateId) {
        this(UUID.randomUUID().toString(), aggregateId, "session.ended", Instant.now());
    }
}
