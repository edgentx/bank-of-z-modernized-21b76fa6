package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

/**
 * Event emitted when a teller session is successfully initiated.
 */
public record SessionStartedEvent(
        String sessionId,
        String tellerId,
        String terminalId,
        Instant startedAt
) implements DomainEvent {

    @Override
    public String type() {
        return "session.started";
    }

    @Override
    public String aggregateId() {
        return sessionId;
    }

    @Override
    public Instant occurredAt() {
        return startedAt;
    }
}
