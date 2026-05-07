package com.example.domain.transaction.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record WithdrawalPostedEvent(
        String aggregateId,
        String accountNumber,
        BigDecimal amount,
        String currency,
        BigDecimal balanceAfter,
        Instant occurredAt
) implements DomainEvent {

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
