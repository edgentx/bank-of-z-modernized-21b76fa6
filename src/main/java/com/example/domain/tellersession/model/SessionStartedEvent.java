package com.example.domain.telllersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a teller session is successfully started.
 */
public record SessionStartedEvent(
        String aggregateId,
        String tellerId,
        String terminalId,
        Instant occurredAt,
        String eventId
) implements DomainEvent {

    public SessionStartedEvent {
        if (eventId == null) eventId = UUID.randomUUID().toString();
        if (occurredAt == null) occurredAt = Instant.now();
    }

    public SessionStartedEvent(String aggregateId, String tellerId, String terminalId, Instant occurredAt) {
        this(aggregateId, tellerId, terminalId, occurredAt, UUID.randomUUID().toString());
    }

    @Override
    public String type() {
        return "session.started";
    }
}
