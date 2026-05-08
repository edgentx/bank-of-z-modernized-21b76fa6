package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event published when a teller session is successfully started.
 */
public record SessionStartedEvent(
        String eventId,
        String aggregateId,
        String tellerId,
        String terminalId,
        Instant occurredAt
) implements DomainEvent {

    public SessionStartedEvent {
        Objects.requireNonNull(eventId, "eventId cannot be null");
        Objects.requireNonNull(aggregateId, "aggregateId cannot be null");
    }

    public static SessionStartedEvent create(String aggregateId, String tellerId, String terminalId) {
        return new SessionStartedEvent(
                UUID.randomUUID().toString(),
                aggregateId,
                tellerId,
                terminalId,
                Instant.now()
        );
    }

    @Override
    public String type() {
        return "session.started";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}
