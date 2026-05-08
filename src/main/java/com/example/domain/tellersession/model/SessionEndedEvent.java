package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record SessionEndedEvent(
        String aggregateId,
        Instant occurredAt,
        String eventId
) implements DomainEvent {
    public SessionEndedEvent(String aggregateId, Instant occurredAt) {
        this(aggregateId, occurredAt, UUID.randomUUID().toString());
    }

    @Override
    public String type() {
        return "session.ended";
    }
}
