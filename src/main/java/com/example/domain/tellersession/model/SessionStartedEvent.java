package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a Teller Session is successfully started.
 * Context: S-18
 */
public record SessionStartedEvent(
    String eventId,
    String aggregateId,
    String tellerId,
    String terminalId,
    Instant occurredAt
) implements DomainEvent {
    public SessionStartedEvent(String aggregateId, String tellerId, String terminalId, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, tellerId, terminalId, occurredAt);
    }

    @Override
    public String type() {
        return "session.started";
    }
}