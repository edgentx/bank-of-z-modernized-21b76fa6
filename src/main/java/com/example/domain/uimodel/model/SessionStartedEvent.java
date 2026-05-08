package com.example.domain.uimodel.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * S-18: Event emitted when a Teller Session is successfully started.
 */
public record SessionStartedEvent(
        String aggregateId,
        String tellerId,
        String terminalId,
        Instant startedAt
) implements DomainEvent {
    @Override
    public String type() {
        return "session.started";
    }

    @Override
    public Instant occurredAt() {
        return startedAt;
    }

    public static SessionStartedEvent create(String aggregateId, String tellerId, String terminalId) {
        return new SessionStartedEvent(aggregateId, tellerId, terminalId, Instant.now());
    }
}
