package com.example.domain.tellermode.model;

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
    boolean isAuthenticated
) implements DomainEvent {
    @Override
    public String type() {
        return "session.started";
    }

    @Override
    public Instant occurredAt() {
        return startedAt;
    }
}
