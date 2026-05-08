package com.example.domain.uinavigation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record SessionStartedEvent(
        String aggregateId,
        String tellerId,
        String terminalId,
        Instant startedAt,
        String eventId
) implements DomainEvent {
    public SessionStartedEvent {
        if (eventId == null) eventId = UUID.randomUUID().toString();
    }

    public SessionStartedEvent(String aggregateId, String tellerId, String terminalId, Instant startedAt) {
        this(aggregateId, tellerId, terminalId, startedAt, UUID.randomUUID().toString());
    }

    @Override
    public String type() {
        return "session.started";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return startedAt;
    }
}