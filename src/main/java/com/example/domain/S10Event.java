package com.example.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain Event for S-10: DepositPosted.
 * Immutable record representing the fact that a deposit was successfully posted.
 */
public record DepositPosted(
        UUID eventId,
        UUID transactionId,
        String accountNumber,
        BigDecimal amount,
        String currency,
        Instant postedAt
) {
    public DepositPosted {
        if (transactionId == null) throw new IllegalArgumentException("transactionId cannot be null");
        if (postedAt == null) postedAt = Instant.now();
    }

    public static DepositPosted create(PostDepositCmd cmd) {
        return new DepositPosted(
                UUID.randomUUID(),
                cmd.transactionId(),
                cmd.accountNumber(),
                cmd.amount(),
                cmd.currency(),
                Instant.now()
        );
    }
}
