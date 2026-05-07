package com.example.domain.navigation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record SessionEndedEvent(
        String aggregateId,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "SessionEndedEvent";
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
