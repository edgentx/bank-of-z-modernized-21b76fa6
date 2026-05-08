package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

public record AccountClosedEvent(String aggregateId, String accountNumber, Instant occurredAt) implements DomainEvent {
    public AccountClosedEvent {
        Objects.requireNonNull(aggregateId, "aggregateId cannot be null");
        Objects.requireNonNull(accountNumber, "accountNumber cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
    }

    @Override
    public String type() {
        return "AccountClosed";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}
