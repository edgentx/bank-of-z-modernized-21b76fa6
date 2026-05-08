package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a TellerSession is successfully terminated.
 */
public record TellerSessionEndedEvent(
        String aggregateId,
        String sessionId,
        Instant occurredAt
) implements DomainEvent {
    public TellerSessionEndedEvent {
        // Defensive defaults in case of bad constructor usage
        if (aggregateId == null) aggregateId = UUID.randomUUID().toString();
        if (occurredAt == null) occurredAt = Instant.now();
    }

    @Override
    public String type() {
        return "session.ended";
    }
}