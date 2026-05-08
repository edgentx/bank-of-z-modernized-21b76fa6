package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record TellerAuthenticatedEvent(
        String eventId,
        String sessionId,
        String tellerId,
        Instant occurredAt
) implements DomainEvent {
    public TellerAuthenticatedEvent {
        if (eventId == null) eventId = UUID.randomUUID().toString();
        if (occurredAt == null) occurredAt = Instant.now();
    }

    public TellerAuthenticatedEvent(String sessionId, String tellerId, Instant occurredAt) {
        this(null, sessionId, tellerId, occurredAt);
    }

    @Override
    public String type() {
        return "teller.authenticated";
    }

    @Override
    public String aggregateId() {
        return sessionId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
