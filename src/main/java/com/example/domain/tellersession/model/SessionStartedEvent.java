package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Objects;

/**
 * Event emitted when a teller session is successfully started.
 */
public record SessionStartedEvent(
        String type,
        String aggregateId,
        String tellerId,
        String terminalId,
        Instant occurredAt
) implements DomainEvent {

    public SessionStartedEvent {
        Objects.requireNonNull(type);
        Objects.requireNonNull(aggregateId);
        Objects.requireNonNull(tellerId);
        Objects.requireNonNull(terminalId);
        Objects.requireNonNull(occurredAt);
    }

    public static SessionStartedEvent of(String aggregateId, String tellerId, String terminalId) {
        return new SessionStartedEvent(
                "SessionStartedEvent",
                aggregateId,
                tellerId,
                terminalId,
                Instant.now()
        );
    }
}
