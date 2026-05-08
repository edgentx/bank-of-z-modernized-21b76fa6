package com.example.domain.teller.model;

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
        Objects.requireNonNull(aggregateId);
        Objects.requireNonNull(tellerId);
        Objects.requireNonNull(terminalId);
        Objects.requireNonNull(occurredAt);
    }

    @Override
    public String type() {
        return "session.started";
    }
}
