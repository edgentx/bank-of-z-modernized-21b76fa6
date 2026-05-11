package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a new account is opened.
 */
public record AccountOpenedEvent(
    String aggregateId,
    String customerId,
    AccountAggregate.AccountType accountType,
    String accountNumber,
    String sortCode,
    BigDecimal initialBalance,
    Instant occurredAt
) implements DomainEvent {

    public AccountOpenedEvent {
        // Ensure eventId for internal tracking if needed, though not strictly part of DomainEvent interface
    }

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
