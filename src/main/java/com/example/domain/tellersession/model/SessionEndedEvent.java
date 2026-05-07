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
            throw new IllegalArgumentException("aggregateId cannot be null or blank");
        }
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("type cannot be null or blank");
        }
        if (occurredAt == null) {
            throw new IllegalArgumentException("occurredAt cannot be null");
        }
    }

    public static SessionEndedEvent create(String sessionId, Instant endTime) {
        return new SessionEndedEvent(
                sessionId,
                "session.ended",
                endTime
        );
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
