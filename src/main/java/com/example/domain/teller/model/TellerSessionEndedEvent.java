package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a teller session is terminated.
 * S-20: user-interface-navigation.
 */
public record TellerSessionEndedEvent(
        String aggregateId,
        String sessionId,
        Instant occurredAt
) implements DomainEvent {
    public TellerSessionEndedEvent {
        Objects.requireNonNull(aggregateId);
        Objects.requireNonNull(sessionId);
        Objects.requireNonNull(occurredAt);
    }

    @Override
    public String type() {
        return "session.ended";
    }
}
