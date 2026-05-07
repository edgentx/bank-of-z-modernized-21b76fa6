package com.example.domain.tellercmd.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record SessionEndedEvent(String aggregateId, Instant occurredAt) implements DomainEvent {
    public SessionEndedEvent {
        if (aggregateId == null || aggregateId.isBlank()) throw new IllegalArgumentException("aggregateId");
        if (occurredAt == null) throw new IllegalArgumentException("occurredAt");
    }

    @Override
    public String type() {
        return "session.ended";
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
