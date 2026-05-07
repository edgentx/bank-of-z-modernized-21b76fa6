package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a teller session starts successfully.
 */
public record SessionStartedEvent(
        String aggregateId,
        String tellerId,
        String terminalId,
        Instant startedAt
) implements DomainEvent {
    public SessionStartedEvent(String aggregateId, String tellerId, String terminalId) {
        this(aggregateId, tellerId, terminalId, Instant.now());
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