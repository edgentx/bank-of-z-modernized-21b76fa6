package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;

public record AccountOpenedEvent(
        String aggregateId,
        String accountNumber,
        AccountType accountType,
        BigDecimal balance,
        Instant occurredAt
) implements DomainEvent {
    @Override public String type() { return "AccountOpened"; }
    @Override public String aggregateId() { return aggregateId; }
    @Override public Instant occurredAt() { return occurredAt; }
}
