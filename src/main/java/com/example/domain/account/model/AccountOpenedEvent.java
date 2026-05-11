package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;

// Supporting event to ensure aggregate has valid state for tests
public record AccountOpenedEvent(
        String aggregateId,
        AccountType type,
        BigDecimal initialBalance,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "account.opened";
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
