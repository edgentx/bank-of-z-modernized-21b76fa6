package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record SessionStartedEvent(
        String aggregateId,
        String tellerId,
        String terminalId,
        Instant occurredAt,
        String eventId
) implements DomainEvent {
    public SessionStartedEvent(String aggregateId, String tellerId, String terminalId, Instant occurredAt) {
        this(aggregateId, tellerId, terminalId, occurredAt, UUID.randomUUID().toString());
    }

    @Override
    public String type() {
        return "session.started";
    }
}
