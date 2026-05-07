package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Event emitted when a teller session is successfully terminated.
 * S-20: SessionEndedEvent.
 */
public class SessionEndedEvent implements DomainEvent {
    private final String aggregateId;
    private final Instant occurredAt;

    public SessionEndedEvent(String aggregateId, Instant occurredAt) {
        this.aggregateId = Objects.requireNonNull(aggregateId);
        this.occurredAt = Objects.requireNonNull(occurredAt);
    }

    @Override
    public String type() {
        return "session.ended";
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
