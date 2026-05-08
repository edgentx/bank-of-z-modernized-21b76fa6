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
    public SessionEndedEvent(String aggregateId, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, "session.ended", occurredAt);
    }
}
