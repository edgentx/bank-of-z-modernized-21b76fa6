package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record SessionStartedEvent(
    String aggregateId,
    String eventId,
    String tellerId,
    String terminalId,
    Instant occurredAt
) implements DomainEvent {
    public SessionStartedEvent {
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId required");
        if (eventId == null) eventId = UUID.randomUUID().toString();
        if (occurredAt == null) occurredAt = Instant.now();
    }

    public SessionStartedEvent(String aggregateId, String tellerId, String terminalId, Instant occurredAt) {
        this(aggregateId, UUID.randomUUID().toString(), tellerId, terminalId, occurredAt);
    }

    @Override public String type() { return "session.started"; }
    @Override public String aggregateId() { return aggregateId; }
    @Override public Instant occurredAt() { return occurredAt; }
}
