package com.example.domain.tellermession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record TellerSessionCreatedEvent(
        String eventId,
        String aggregateId,
        String tellerId,
        Instant occurredAt
) implements DomainEvent {
    public TellerSessionCreatedEvent(String aggregateId, String tellerId, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, tellerId, occurredAt);
    }

    @Override
    public String type() {
        return "session.created";
    }
}
