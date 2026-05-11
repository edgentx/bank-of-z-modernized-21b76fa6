package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record SessionStartedEvent(
    String eventId,
    String aggregateId,
    Instant occurredAt,
    String tellerId,
    String terminalId
) implements DomainEvent {
    public SessionStartedEvent {
        // Basic validation logic could go here if needed
    }

    public SessionStartedEvent(String aggregateId, String tellerId, String terminalId) {
        this(UUID.randomUUID().toString(), aggregateId, Instant.now(), tellerId, terminalId);
    }

    @Override
    public String type() {
        return "session.started";
    }
}
