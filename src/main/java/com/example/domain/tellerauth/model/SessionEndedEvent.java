package com.example.domain.tellerauth.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record SessionEndedEvent(
        String aggregateId,
        String type,
        Instant occurredAt
) implements DomainEvent {
    public SessionEndedEvent(String aggregateId, Instant occurredAt) {
        this(aggregateId, "session.ended", occurredAt);
    }
}
