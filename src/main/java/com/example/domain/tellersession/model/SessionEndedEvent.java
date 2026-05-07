package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a TellerSession is successfully terminated.
 */
public record SessionEndedEvent(
        String aggregateId,
        Instant occurredAt,
        String reason
) implements DomainEvent {
    public SessionEndedEvent {
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId cannot be null");
        if (occurredAt == null) occurredAt = Instant.now();
    }

    public SessionEndedEvent(String aggregateId) {
        this(aggregateId, Instant.now(), "USER_LOGOUT");
    }

    @Override
    public String type() {
        return "session.ended";
    }
}
