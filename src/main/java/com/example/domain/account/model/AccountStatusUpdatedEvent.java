package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Event emitted when an Account status is updated.
 */
public record AccountStatusUpdatedEvent(
        String aggregateId,
        String accountNumber,
        AccountAggregate.AccountStatus oldStatus,
        AccountAggregate.AccountStatus newStatus,
        Instant occurredAt
) implements DomainEvent {

    public AccountStatusUpdatedEvent {
        Objects.requireNonNull(aggregateId, "aggregateId required");
        Objects.requireNonNull(accountNumber, "accountNumber required");
        Objects.requireNonNull(oldStatus, "oldStatus required");
        Objects.requireNonNull(newStatus, "newStatus required");
        Objects.requireNonNull(occurredAt, "occurredAt required");
    }

    @Override
    public String type() {
        return "account.status.updated";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}
