package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a new account is opened.
 */
public record AccountOpenedEvent(
        String eventId,
        String aggregateId,
        String customerId,
        String accountType,
        BigDecimal balance,
        String sortCode,
        String accountNumber,
        Instant occurredAt
) implements DomainEvent {
    public AccountOpenedEvent(String aggregateId, String customerId, String accountType, BigDecimal balance, String sortCode, String accountNumber, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, customerId, accountType, balance, sortCode, accountNumber, occurredAt);
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
