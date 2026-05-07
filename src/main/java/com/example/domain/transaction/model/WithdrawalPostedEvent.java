package com.example.domain.transaction.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public record WithdrawalPostedEvent(String aggregateId, String accountNumber, BigDecimal amount, String currency, Instant occurredAt) implements DomainEvent {
    public WithdrawalPostedEvent {
        Objects.requireNonNull(aggregateId);
        Objects.requireNonNull(accountNumber);
        Objects.requireNonNull(amount);
        Objects.requireNonNull(occurredAt);
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
