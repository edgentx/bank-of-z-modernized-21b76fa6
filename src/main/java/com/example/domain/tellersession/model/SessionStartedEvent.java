package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a teller session is successfully started.
 */
public record SessionStartedEvent(String aggregateId, String tellerId, String terminalId, Instant occurredAt) implements DomainEvent {
    public SessionStartedEvent {
        // Validation implies basic non-null checks, though records handle some of this implicitly if constructed correctly.
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId cannot be null");
        if (occurredAt == null) occurredAt = Instant.now();
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
        return occurredAt;
    }
}
