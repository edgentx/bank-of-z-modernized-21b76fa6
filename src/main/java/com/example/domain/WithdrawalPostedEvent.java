package com.example.domain;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

public record WithdrawalPostedEvent(
        UUID eventId,
        long timestamp,
        String transactionId,
        String accountNumber,
        BigDecimal amount,
        Currency currency
) implements DomainEvent {

    public WithdrawalPostedEvent {
        if (amount == null) throw new IllegalArgumentException("Amount cannot be null");
    }

    @Override
    public UUID eventId() {
        return eventId;
    }

    @Override
    public long timestamp() {
        return timestamp;
    }
}