package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a TellerSession is successfully terminated.
 */
public record TellerSessionEndedEvent(
        String eventId,
        String aggregateId,
        Instant occurredAt
) implements DomainEvent {
    public TellerSessionEndedEvent {
        if (eventId == null) eventId = UUID.randomUUID().toString();
    }

    public TellerSessionEndedEvent(String aggregateId) {
        this(UUID.randomUUID().toString(), aggregateId, Instant.now());
    }

    @Override
    public String type() {
        return "session.ended";
    }
}
