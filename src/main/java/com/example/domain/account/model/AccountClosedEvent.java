package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record AccountClosedEvent(
    String aggregateId,
    Instant occurredAt
) implements DomainEvent {
    public AccountClosedEvent {
        // Ensure values are non-null
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId cannot be null");
    }

    public AccountClosedEvent(String aggregateId) {
        this(aggregateId, Instant.now());
    }

    @Override
    public String type() {
        return "account.closed";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
