package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record SessionEndedEvent(
        String aggregateId,
        String type,
        Instant occurredAt,
        String eventId
) implements DomainEvent {
    public SessionEndedEvent {
        if (type == null) type = "session.ended";
        if (occurredAt == null) occurredAt = Instant.now();
        if (eventId == null) eventId = UUID.randomUUID().toString();
    }

    // Convenience constructor
    public SessionEndedEvent(String aggregateId, Instant occurredAt) {
        this(aggregateId, "session.ended", occurredAt, UUID.randomUUID().toString());
    }
}
