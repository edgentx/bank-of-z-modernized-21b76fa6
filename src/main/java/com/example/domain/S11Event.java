package com.example.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Event hierarchy for Story S-11.
 * Events represent state changes that have occurred.
 */
public sealed interface S11Event permits S11Event.WithdrawalPosted {

    UUID eventId() default UUID.randomUUID();
    Instant timestamp() default Instant.now();

    record WithdrawalPosted(
        UUID eventId,
        Instant timestamp,
        UUID transactionId,
        UUID accountId,
        BigDecimal amount,
        String currency
    ) implements S11Event {
        public WithdrawalPosted {
            // Defensive copy if needed, though records are immutable
        }
    }

}
