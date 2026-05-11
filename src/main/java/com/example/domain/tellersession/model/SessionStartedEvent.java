package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record SessionStartedEvent(
        String type,
        String aggregateId,
        String tellerId,
        String terminalId,
        Instant occurredAt
) implements DomainEvent {
    public SessionStartedEvent {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("type cannot be null");
        }
        if (aggregateId == null || aggregateId.isBlank()) {
            throw new IllegalArgumentException("aggregateId cannot be null");
        }
        if (occurredAt == null) {
            throw new IllegalArgumentException("occurredAt cannot be null");
        }
    }

    public static SessionStartedEvent create(String aggregateId, String tellerId, String terminalId, Instant timestamp) {
        return new SessionStartedEvent(
                "SessionStarted",
                aggregateId,
                tellerId,
                terminalId,
                timestamp
        );
    }
}
