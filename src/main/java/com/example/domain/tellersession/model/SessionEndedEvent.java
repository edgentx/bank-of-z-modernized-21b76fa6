package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a TellerSession is successfully terminated.
 */
public record SessionEndedEvent(
        String aggregateId,
        String type,
        Instant occurredAt
) implements DomainEvent {
    public SessionEndedEvent {
        if (aggregateId == null || aggregateId.isBlank()) {
            throw new IllegalArgumentException("aggregateId required");
        }
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("type required");
        }
        if (occurredAt == null) {
            throw new IllegalArgumentException("occurredAt required");
        }
    }

    public static SessionEndedEvent create(String aggregateId, Instant endedAt) {
        return new SessionEndedEvent(aggregateId, "session.ended", endedAt);
    }
}
