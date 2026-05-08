package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record TellerLoggedInEvent(
    String aggregateId,
    String tellerId,
    Instant occurredAt
) implements DomainEvent {
    public TellerLoggedInEvent {
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId required");
    }

    public TellerLoggedInEvent(String aggregateId, String tellerId, Instant occurredAt) {
        this(
            aggregateId != null ? aggregateId : UUID.randomUUID().toString(),
            tellerId,
            occurredAt != null ? occurredAt : Instant.now()
        );
    }

    @Override
    public String type() {
        return "teller.logged.in";
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
