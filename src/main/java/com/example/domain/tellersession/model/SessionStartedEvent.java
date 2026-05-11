package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a teller session is successfully started.
 */
public record SessionStartedEvent(
        String type,
        String aggregateId,
        Instant occurredAt,
        String tellerId,
        String terminalId
) implements DomainEvent {

    public SessionStartedEvent {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("type cannot be null or blank");
        }
    }

    public SessionStartedEvent(String aggregateId, String tellerId, String terminalId) {
        this("session.started", aggregateId, Instant.now(), tellerId, terminalId);
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
