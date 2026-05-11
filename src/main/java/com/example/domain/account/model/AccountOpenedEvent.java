package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Event emitted when a new account is opened.
 */
public record AccountOpenedEvent(
        String aggregateId,
        String customerId,
        AccountAggregate.AccountType accountType,
        BigDecimal balance,
        String sortCode,
        Instant occurredAt
) implements DomainEvent {

    @Override
    public String type() {
        return "account.opened";
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