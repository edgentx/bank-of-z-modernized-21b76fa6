package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record AccountClosedEvent(
        String eventId,
        String aggregateId,
        Instant occurredAt
) implements DomainEvent {

    public AccountClosedEvent(String aggregateId, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, occurredAt);
    }

    @Override
    public String type() {
        return "account.closed";
    }
}
