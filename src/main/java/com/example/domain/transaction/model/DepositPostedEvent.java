package com.example.domain.transaction.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record DepositPostedEvent(
        String eventId,
        String aggregateId,
        String accountNumber,
        BigDecimal amount,
        String currency,
        Instant occurredAt
) implements DomainEvent {

    public DepositPostedEvent {
        Objects.requireNonNull(eventId);
        Objects.requireNonNull(aggregateId);
        Objects.requireNonNull(accountNumber);
        Objects.requireNonNull(amount);
        Objects.requireNonNull(currency);
        Objects.requireNonNull(occurredAt);
    }

    public static DepositPostedEvent create(String aggregateId, String accountNumber, BigDecimal amount, String currency) {
        return new DepositPostedEvent(
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
        return "deposit.posted";
    }
}