package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record AccountStatusUpdatedEvent(String aggregateId, AccountAggregate.AccountStatus newStatus, Instant occurredAt) implements DomainEvent {
    @Override
    public String type() {
        return "account.status.updated";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}
