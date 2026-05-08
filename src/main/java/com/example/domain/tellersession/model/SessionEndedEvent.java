package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a Teller Session is successfully terminated.
 * Sensitive state is cleared upon this event application.
 */
public record SessionEndedEvent(
        String aggregateId,
        String endedBy,
        Instant endedAt
) implements DomainEvent {
    @Override
    public String type() {
        return "session.ended";
    }

    @Override
    public Instant occurredAt() {
        return endedAt;
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}
