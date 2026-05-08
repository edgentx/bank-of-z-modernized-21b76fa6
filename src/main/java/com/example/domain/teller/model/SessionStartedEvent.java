package com.example.domain.teller.model;

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
    Instant timeoutAt
) implements DomainEvent {

    @Override
    public String type() {
        return "teller.session.started";
    }

    @Override
    public Instant occurredAt() {
        return startedAt;
    }
}
