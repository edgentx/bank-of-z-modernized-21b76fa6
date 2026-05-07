package com.example.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a deposit is successfully posted.
 */
public record DepositPostedEvent(UUID eventId, UUID transactionId, String accountNumber, BigDecimal amount, String currency, Instant postedAt) {
    public DepositPostedEvent {
        if (postedAt == null) {
            postedAt = Instant.now();
        }
    }

    public static DepositPostedEvent create(UUID transactionId, String accountNumber, BigDecimal amount, String currency) {
        return new DepositPostedEvent(
            UUID.randomUUID(),
            transactionId,
            accountNumber,
            amount,
            currency,
            Instant.now()
        );
    }
}
