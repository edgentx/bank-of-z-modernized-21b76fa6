package com.example.domain.uimodel.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a Teller Session is successfully ended.
 */
public record SessionEndedEvent(
        String aggregateId,
        String tellerId,
        Instant occurredAt
) implements DomainEvent {

    public SessionEndedEvent {
        if (aggregateId == null || aggregateId.isBlank()) {
            throw new IllegalArgumentException("aggregateId cannot be null or blank");
        }
        if (occurredAt == null) {
            throw new IllegalArgumentException("occurredAt cannot be null");
        }
    }

    @Override
    public String type() {
        return "session.ended";
    }
}
