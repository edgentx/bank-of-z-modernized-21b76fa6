package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record SessionEndedEvent(
        String aggregateId,
        String type,
        Instant occurredAt
) implements DomainEvent {
    public SessionEndedEvent(String aggregateId, Instant endedAt) {
        this(aggregateId, "session.ended", endedAt);
    }

    @Override
    public String type() {
        return type;
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