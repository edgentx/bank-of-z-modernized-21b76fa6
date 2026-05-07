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
        Objects.requireNonNull(eventId, "eventId required");
        Objects.requireNonNull(aggregateId, "aggregateId required");
        Objects.requireNonNull(accountNumber, "accountNumber required");
        Objects.requireNonNull(amount, "amount required");
        Objects.requireNonNull(currency, "currency required");
        Objects.requireNonNull(occurredAt, "occurredAt required");
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

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
