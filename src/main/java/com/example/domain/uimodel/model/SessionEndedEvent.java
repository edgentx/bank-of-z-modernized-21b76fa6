package com.example.domain.uimodel.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record SessionEndedEvent(
    String aggregateId,
    Instant occurredAt
) implements DomainEvent {
    public SessionEndedEvent(String aggregateId, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.occurredAt = occurredAt;
    }

    @Override
    public String type() {
        return "session.ended";
    }
}
