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
    String navigationContext,
    Instant occurredAt
) implements DomainEvent {

    public SessionStartedEvent {
        // Defensive defaults/safety if needed, though records handle nulls explicitly
    }

    @Override
    public String type() {
        return "session.started";
    }
}
