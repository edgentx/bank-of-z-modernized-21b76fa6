package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when an account status is updated.
 */
public record AccountStatusUpdatedEvent(
        String aggregateId,
        String oldStatus,
        String newStatus,
        Instant occurredAt
) implements DomainEvent {

    public AccountStatusUpdatedEvent {
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId required");
    }

    @Override
    public String type() {
        return "account.status.updated";
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
