package com.example.domain.tellermgmt.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a teller session is successfully started.
 */
public record SessionStartedEvent(
    String aggregateId,
    String tellerId,
    String terminalId,
    Instant occurredAt
) implements DomainEvent {
    public SessionStartedEvent {
        // Basic validation, though constructors usually guarantee this
    }

    @Override
    public String type() {
        return "session.started";
    }

    public static SessionStartedEvent create(String aggregateId, String tellerId, String terminalId) {
        return new SessionStartedEvent(
            aggregateId,
            tellerId,
            terminalId,
            Instant.now()
        );
    }
}
