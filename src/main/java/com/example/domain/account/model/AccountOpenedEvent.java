package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public record AccountOpenedEvent(
    String aggregateId,
    String customerId,
    String accountType,
    String accountNumber,
    BigDecimal balance,
    String sortCode,
    Instant occurredAt
) implements DomainEvent {

    public AccountOpenedEvent {
        Objects.requireNonNull(aggregateId, "aggregateId cannot be null");
        Objects.requireNonNull(customerId, "customerId cannot be null");
        Objects.requireNonNull(accountType, "accountType cannot be null");
        Objects.requireNonNull(accountNumber, "accountNumber cannot be null");
        Objects.requireNonNull(balance, "balance cannot be null");
        Objects.requireNonNull(sortCode, "sortCode cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
    }

    @Override
    public String type() {
        return "account.opened";
    }

    public String accountNumber() { return accountNumber; }
    public BigDecimal balance() { return balance; }
    public String aggregateId() { return aggregateId; }
    public Instant occurredAt() { return occurredAt; }
}
