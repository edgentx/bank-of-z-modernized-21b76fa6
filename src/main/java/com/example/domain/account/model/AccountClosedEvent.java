package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

public record AccountClosedEvent(
        String aggregateId,
        String accountNumber,
        Instant occurredAt
) implements DomainEvent {
    public AccountClosedEvent {
        Objects.requireNonNull(aggregateId);
        Objects.requireNonNull(accountNumber);
        Objects.requireNonNull(occurredAt);
    }

    @Override
    public String type() {
        return "account.closed";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}
