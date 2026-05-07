package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record SessionEndedEvent(
        String aggregateId,
        String tellerId,
        Instant occurredAt
) implements DomainEvent {
    public SessionEndedEvent {
        // Ensure aggregateId is set
        if (aggregateId == null) {
            aggregateId = UUID.randomUUID().toString();
        }
    }

    @Override
    public String type() {
        return "session.ended";
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
