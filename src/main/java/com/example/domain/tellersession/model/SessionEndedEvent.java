package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a Teller session is ended.
 */
public record SessionEndedEvent(
        String aggregateId,
        String endedBy,
        Instant endedAt
) implements DomainEvent {
    public SessionEndedEvent {
        if (aggregateId == null || aggregateId.isBlank()) {
            throw new IllegalArgumentException("aggregateId cannot be null or blank");
        }
        if (endedBy == null || endedBy.isBlank()) {
            throw new IllegalArgumentException("endedBy cannot be null or blank");
        }
        if (endedAt == null) {
            throw new IllegalArgumentException("endedAt cannot be null");
        }
    }

    @Override
    public String type() {
        return "session.ended";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return endedAt;
    }
}