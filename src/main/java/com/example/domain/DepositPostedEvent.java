package com.example.domain;

import java.math.BigDecimal;
import java.util.UUID;
import java.time.Instant;

/**
 * Event emitted when a deposit is successfully posted to the ledger.
 * Implements DomainEvent marker interface.
 */
public record DepositPostedEvent(
    UUID transactionId,
    String accountNumber,
    BigDecimal amount,
    String currencyCode,
    Instant occurredAt
) implements DomainEvent {

    public DepositPostedEvent(UUID transactionId, String accountNumber, BigDecimal amount, String currencyCode) {
        this(transactionId, accountNumber, amount, currencyCode, Instant.now());
    }
}
