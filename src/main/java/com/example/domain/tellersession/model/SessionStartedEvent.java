package com.example.domain.tellersession.model;

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
    String terminalId
) implements DomainEvent {

    public SessionStartedEvent(String aggregateId, String tellerId, String terminalId) {
        this(
            "SessionStartedEvent",
            aggregateId,
            Instant.now(),
            tellerId,
            terminalId
        );
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
