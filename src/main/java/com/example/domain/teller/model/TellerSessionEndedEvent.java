package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record TellerSessionEndedEvent(
        String aggregateId,
        String tellerId,
        Instant occurredAt
) implements DomainEvent {
    public TellerSessionEndedEvent(String sessionId, String tellerId) {
        this(sessionId, tellerId, Instant.now());
    }

    @Override
    public String type() {
        return "session.ended";
    }
}
