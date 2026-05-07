package com.example.domain;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Event emitted when a deposit is successfully posted.
 */
public record DepositPostedEvent(
        String transactionId,
        String accountNumber,
        BigDecimal amount,
        String currency,
        Instant postedAt
) {
    public DepositPostedEvent {
        // Defensive copy for immutability
        amount = amount != null ? amount : BigDecimal.ZERO;
    }
}