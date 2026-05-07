package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record SessionEndedEvent(
        String aggregateId,
        String tellerId,
        Instant occurredAt
) implements DomainEvent {
    public SessionEndedEvent {
        // Basic validation if needed, though record handles it
    }

    @Override
    public String type() {
        return "session.ended";
    }
}