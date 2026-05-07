package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record SessionEndedEvent(
        String aggregateId,
        String eventType,
        Instant occurredAt
) implements DomainEvent {
    public SessionEndedEvent(String aggregateId) {
        this(aggregateId, "session.ended", Instant.now());
    }

    @Override
    public String type() {
        return eventType;
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
