package com.example.domain.transaction.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;

public record DepositPostedEvent(
        String aggregateId,
        String accountNumber,
        BigDecimal amount,
        String currency,
        Instant occurredAt
) implements DomainEvent {

    public DepositPostedEvent(String aggregateId, String accountNumber, BigDecimal amount, String currency) {
        this(aggregateId, accountNumber, amount, currency, Instant.now());
    }

    @Override
    public String type() {
        return "deposit.posted";
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
