package com.example.domain.uinavigation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a Teller Session is successfully started.
 * ID: S-18
 */
public record SessionStartedEvent(
    String aggregateId,
    String tellerId,
    String terminalId,
    Instant occurredAt,
    String eventId
) implements DomainEvent {

    public SessionStartedEvent(String aggregateId, String tellerId, String terminalId, Instant occurredAt) {
        this(aggregateId, tellerId, terminalId, occurredAt, UUID.randomUUID().toString());
    }

    @Override
    public String type() {
        return "session.started";
    }
}
