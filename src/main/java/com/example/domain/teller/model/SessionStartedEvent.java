package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a Teller Session is successfully started.
 */
public record SessionStartedEvent(
    String aggregateId,
    String tellerId,
    String terminalId,
    Instant startedAt,
    UUID eventId
) implements DomainEvent {

    public SessionStartedEvent(String aggregateId, String tellerId, String terminalId, Instant startedAt) {
        this(aggregateId, tellerId, terminalId, startedAt, UUID.randomUUID());
    }

    @Override
    public String type() {
        return "session.started";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return startedAt;
    }
}
