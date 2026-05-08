package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a Teller Session is successfully initiated.
 */
public record SessionStartedEvent(
        String aggregateId,
        String tellerId,
        String terminalId,
        Instant occurredAt
) implements DomainEvent {

    @Override
    public String type() {
        return "session.started";
    }

    // Constructor to handle defaults if needed, though record is fine.
    public SessionStartedEvent {
        if (aggregateId == null) aggregateId = UUID.randomUUID().toString();
        if (occurredAt == null) occurredAt = Instant.now();
    }
}
