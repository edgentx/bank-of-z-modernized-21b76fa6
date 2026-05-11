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
        Instant occurredAt,
        String tellerId,
        String terminalId
) implements DomainEvent {

    public SessionStartedEvent {
        Objects.requireNonNull(type, "type cannot be null");
        Objects.requireNonNull(aggregateId, "aggregateId cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
        Objects.requireNonNull(tellerId, "tellerId cannot be null");
        Objects.requireNonNull(terminalId, "terminalId cannot be null");
    }

    public static SessionStartedEvent of(String aggregateId, String tellerId, String terminalId) {
        return new SessionStartedEvent(
                "tellersession.session.started",
                aggregateId,
                Instant.now(),
                tellerId,
                terminalId
        );
    }
}
