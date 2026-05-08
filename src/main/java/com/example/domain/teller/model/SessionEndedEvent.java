package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Domain Event emitted when a Teller Session ends successfully.
 * S-20.
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
