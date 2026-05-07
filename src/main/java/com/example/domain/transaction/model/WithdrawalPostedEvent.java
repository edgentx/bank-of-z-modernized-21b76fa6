package com.example.domain.transaction.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record WithdrawalPostedEvent(
    String eventId,
    String aggregateId,
    String accountNumber,
    BigDecimal amount,
    String currency,
    Instant occurredAt
) implements DomainEvent {
    public WithdrawalPostedEvent(String aggregateId, String accountNumber, BigDecimal amount, String currency, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, accountNumber, amount, currency, occurredAt);
    }

    @Override
    public String type() {
        return "withdrawal.posted";
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
