package com.example.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record DepositPostedEvent(UUID eventId, TransactionId transactionId, String accountNumber, BigDecimal amount, String currency, Instant timestamp) {
    public DepositPostedEvent {
        if (eventId == null) eventId = UUID.randomUUID();
        if (timestamp == null) timestamp = Instant.now();
    }

    public static DepositPostedEvent create(TransactionId transactionId, String accountNumber, BigDecimal amount, String currency) {
        return new DepositPostedEvent(null, transactionId, accountNumber, amount, currency, null);
    }
}
