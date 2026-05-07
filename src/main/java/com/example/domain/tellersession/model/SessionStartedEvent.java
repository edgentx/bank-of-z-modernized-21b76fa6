package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event representing the start of a session. Used to hydrate the aggregate for testing.
 */
public record SessionStartedEvent(
        String aggregateId,
        String tellerId,
        Instant occurredAt
) implements DomainEvent {

    public SessionStartedEvent {
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId required");
        if (tellerId == null) throw new IllegalArgumentException("tellerId required");
        if (occurredAt == null) occurredAt = Instant.now();
    }

    @Override
    public String type() {
        return "session.started";
    }
}
