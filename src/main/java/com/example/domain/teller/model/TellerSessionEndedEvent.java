package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record TellerSessionEndedEvent(
    String aggregateId,
    Instant occurredAt
) implements DomainEvent {
    public TellerSessionEndedEvent {
        // Validate
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId required");
    }
    @Override public String type() { return "session.ended"; }
    @Override public String aggregateId() { return aggregateId; }
    @Override public Instant occurredAt() { return occurredAt; }
}
