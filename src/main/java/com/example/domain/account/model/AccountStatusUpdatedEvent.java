package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record AccountStatusUpdatedEvent(
    String accountNumber,
    AccountAggregate.Status oldStatus,
    AccountAggregate.Status newStatus,
    Instant occurredAt
) implements DomainEvent {

    @Override
    public String type() {
        return "account.status.updated";
    }

    @Override
    public String aggregateId() {
        return accountNumber;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
