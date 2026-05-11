package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * Event emitted when a new account is opened.
 */
public record AccountOpenedEvent(
    String aggregateId,
    String customerId,
    String accountType,
    BigDecimal initialDeposit,
    String sortCode,
    Instant occurredAt
) implements DomainEvent {

    public AccountOpenedEvent {
        Objects.requireNonNull(aggregateId, "aggregateId required");
        Objects.requireNonNull(occurredAt, "occurredAt required");
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
