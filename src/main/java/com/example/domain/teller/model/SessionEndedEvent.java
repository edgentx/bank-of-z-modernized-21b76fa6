package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record SessionEndedEvent(String aggregateId, Instant occurredAt) implements DomainEvent {
    public static final String TYPE = "session.ended";

    @Override
    public String type() {
        return TYPE;
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
