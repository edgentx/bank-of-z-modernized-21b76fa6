package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record SessionEndedEvent(
    String aggregateId,
    String sessionId,
    Instant occurredAt
) implements DomainEvent {
    public SessionEndedEvent(String aggregateId, String sessionId, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.sessionId = sessionId;
        this.occurredAt = occurredAt;
    }

    @Override
    public String type() {
        return "session.ended";
    }
}
