package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record TellerSessionEndedEvent(String aggregateId, Instant occurredAt) implements DomainEvent {
    public TellerSessionEndedEvent {
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId required");
    }

    public TellerSessionEndedEvent(String aggregateId) {
        this(aggregateId, Instant.now());
    }

    @Override public String type() { return "teller.session.ended"; }
    @Override public String aggregateId() { return aggregateId; }
}