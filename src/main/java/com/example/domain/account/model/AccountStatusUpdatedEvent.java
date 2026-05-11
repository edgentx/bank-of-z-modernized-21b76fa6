package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when an account status changes.
 * S-6
 */
public record AccountStatusUpdatedEvent(
    String aggregateId,
    AccountStatus oldStatus,
    AccountStatus newStatus,
    Instant occurredAt
) implements DomainEvent {

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
