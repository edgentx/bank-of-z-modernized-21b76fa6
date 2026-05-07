package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record TellerSessionAuthenticatedEvent(
        String aggregateId,
        String tellerId,
        Instant occurredAt
) implements DomainEvent {
    public TellerSessionAuthenticatedEvent {
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId required");
    }

    @Override
    public String type() {
        return "teller.session.authenticated";
    }
}
