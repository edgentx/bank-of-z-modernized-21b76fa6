package com.example.domain.tellermenu.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record TellerSessionInitializedEvent(
        String eventId,
        String aggregateId,
        String tellerId,
        Instant occurredAt
) implements DomainEvent {
    public TellerSessionInitializedEvent {
        if (eventId == null) eventId = UUID.randomUUID().toString();
        if (occurredAt == null) occurredAt = Instant.now();
    }

    public TellerSessionInitializedEvent(String aggregateId, String tellerId, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, tellerId, occurredAt);
    }

    @Override public String type() { return "teller.session.initialized"; }
    @Override public String aggregateId() { return aggregateId; }
    @Override public Instant occurredAt() { return occurredAt; }
}