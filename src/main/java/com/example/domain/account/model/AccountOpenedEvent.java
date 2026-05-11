package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AccountOpenedEvent(
    String aggregateId,
    String customerId,
    String accountType,
    String sortCode,
    BigDecimal initialBalance,
    Instant occurredAt
) implements DomainEvent {

    public AccountOpenedEvent {
        // Ensure occurredAt is not null for the record
        if (occurredAt == null) occurredAt = Instant.now();
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
