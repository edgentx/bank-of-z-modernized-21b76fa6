package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a teller session is successfully started.
 */
public record SessionStartedEvent(
        String type,
        String aggregateId,
        Instant occurredAt,
        String tellerId,
        String terminalId,
        String state
) implements DomainEvent {
    public SessionStartedEvent(String aggregateId, String tellerId, String terminalId, Instant occurredAt) {
        this(
                "session.started",
                aggregateId,
                occurredAt,
                tellerId,
                terminalId,
                "ACTIVE"
        );
    }
}
