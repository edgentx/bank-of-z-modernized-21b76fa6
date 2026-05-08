package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a Teller Session is successfully initiated.
 */
public record SessionStartedEvent(
        String eventId,
        String aggregateId,
        String tellerId,
        String terminalId,
        Instant occurredAt
) implements DomainEvent {
    public SessionStartedEvent {
        if (eventId == null) eventId = UUID.randomUUID().toString();
        if (occurredAt == null) occurredAt = Instant.now();
    }

    public SessionStartedEvent(String aggregateId, String tellerId, String terminalId) {
        this(UUID.randomUUID().toString(), aggregateId, tellerId, terminalId, Instant.now());
    }

    @Override
    public String type() {
        return "teller.session.started";
    }
}
