package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when an account's status is successfully updated.
 */
public record AccountStatusUpdatedEvent(
        String aggregateId,
        String accountNumber,
        AccountStatus oldStatus,
        AccountStatus newStatus,
        Instant occurredAt
) implements DomainEvent {
    public AccountStatusUpdatedEvent {
        // Defensive copy for immutability if necessary, though records are immutable
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
