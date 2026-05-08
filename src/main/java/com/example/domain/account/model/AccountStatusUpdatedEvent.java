package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when an account status is updated.
 */
public record AccountStatusUpdatedEvent(
        String aggregateId,
        AccountAggregate.AccountStatus oldStatus,
        AccountAggregate.AccountStatus newStatus,
        Instant occurredAt
) implements DomainEvent {

    @Override
    public String type() {
        return "account.status.updated";
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}