package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record SessionEndedEvent(
    String aggregateId,
    Instant occurredAt
) implements DomainEvent {
    public SessionEndedEvent {
        // Validation defaults handled by factory method in aggregate
    }

    @Override
    public String type() {
        return "session.ended";
    }
}
