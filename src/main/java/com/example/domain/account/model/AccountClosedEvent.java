package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Event emitted when an account is closed.
 */
public record AccountClosedEvent(
        String aggregateId,
        String type,
        Instant occurredAt
) implements DomainEvent {

    public AccountClosedEvent(String aggregateId, Instant occurredAt) {
        this(aggregateId, "account.closed", occurredAt);
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
