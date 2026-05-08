package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record SessionEndedEvent(
        String aggregateId,
        String endedBy,
        Instant occurredAt
) implements DomainEvent {
    public SessionEndedEvent {
        // Ensure valid state
    }

    @Override
    public String type() {
        return "session.ended";
    }
}