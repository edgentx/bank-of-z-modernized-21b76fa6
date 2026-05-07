package com.example.domain.transaction.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record WithdrawalPostedEvent(
        String eventId,
        String aggregateId,
        String accountNumber,
        BigDecimal amount,
        String currency,
        Instant occurredAt
) implements DomainEvent {

    public WithdrawalPostedEvent {
        Objects.requireNonNull(eventId, "eventId cannot be null");
        Objects.requireNonNull(aggregateId, "aggregateId cannot be null");
        Objects.requireNonNull(accountNumber, "accountNumber cannot be null");
        Objects.requireNonNull(amount, "amount cannot be null");
        Objects.requireNonNull(currency, "currency cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
    }

    public static WithdrawalPostedEvent create(String aggregateId, String accountNumber, BigDecimal amount, String currency) {
        return new WithdrawalPostedEvent(
                UUID.randomUUID().toString(),
                aggregateId,
                accountNumber,
                amount,
                currency,
                Instant.now()
        );
    }

    @Override
    public String type() {
        return "withdrawal.posted";
    }
}