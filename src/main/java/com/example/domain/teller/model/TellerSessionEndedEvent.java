package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a Teller Session is terminated.
 * S-20: user-interface-navigation
 */
public record TellerSessionEndedEvent(
    String aggregateId,
    Instant occurredAt
) implements DomainEvent {

    public TellerSessionEndedEvent {
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId required");
    }

    public TellerSessionEndedEvent(String aggregateId) {
        this(aggregateId, Instant.now());
    }

    @Override
    public String type() {
        return "session.ended";
    }
}
