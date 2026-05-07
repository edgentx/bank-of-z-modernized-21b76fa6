package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a Teller Session is successfully terminated.
 */
public record SessionEndedEvent(
        String eventId,
        String aggregateId,
        String tellerId,
        String reason,
        Instant occurredAt
) implements DomainEvent {

    public SessionEndedEvent {
        if (eventId == null || eventId.isBlank()) {
            eventId = UUID.randomUUID().toString();
        }
    }

    public SessionEndedEvent(String aggregateId, String tellerId, String reason, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, tellerId, reason, occurredAt);
    }

    @Override
    public String type() {
        return "session.ended";
    }
}
