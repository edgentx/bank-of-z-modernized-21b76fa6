package com.example.domain.uinavigation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record SessionStartedEvent(
        String type,
        String aggregateId,
        Instant occurredAt,
        String tellerId,
        String terminalId
) implements DomainEvent {
    public SessionStartedEvent {
        if (type == null) type = "session.started";
        Objects.requireNonNull(aggregateId, "aggregateId required");
        Objects.requireNonNull(occurredAt, "occurredAt required");
    }

    public SessionStartedEvent(String aggregateId, String tellerId, String terminalId, Instant occurredAt) {
        this("session.started", aggregateId, occurredAt, tellerId, terminalId);
    }
}
