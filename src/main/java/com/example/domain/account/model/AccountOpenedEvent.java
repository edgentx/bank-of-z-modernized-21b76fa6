package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when an account is successfully opened.
 * S-5: AccountOpenedEvent
 */
public record AccountOpenedEvent(
    String eventId,
    String aggregateId,
    String customerId,
    String accountType,
    BigDecimal initialBalance,
    String sortCode,
    Instant occurredAt
) implements DomainEvent {
    public AccountOpenedEvent(String aggregateId, String customerId, String accountType, BigDecimal initialBalance, String sortCode, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, customerId, accountType, initialBalance, sortCode, occurredAt);
    }

    @Override
    public String type() {
        return "account.opened";
    }
}
