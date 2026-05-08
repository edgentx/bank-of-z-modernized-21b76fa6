package com.example.domain.tellersession.model;

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
    Instant startedAt,
    UUID eventId
) implements DomainEvent {

    public SessionStartedEvent {
        if (eventId == null) eventId = UUID.randomUUID();
    }

    @Override
    public String type() {
        return "session.started";
    }

    @Override
    public Instant occurredAt() {
        return startedAt;
    }
}