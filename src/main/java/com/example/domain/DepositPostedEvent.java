package com.example.domain;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

/**
 * Event emitted when a deposit is successfully posted.
 * Uses Java Record.
 */
public record DepositPostedEvent(
    UUID transactionId,
    String accountNumber,
    BigDecimal amount,
    Currency currency,
    BigDecimal balanceAfter
) {
    public DepositPostedEvent {
        if (transactionId == null) throw new IllegalArgumentException("transactionId cannot be null");
        if (accountNumber == null) throw new IllegalArgumentException("accountNumber cannot be null");
        if (amount == null) throw new IllegalArgumentException("amount cannot be null");
        if (currency == null) throw new IllegalArgumentException("currency cannot be null");
        if (balanceAfter == null) throw new IllegalArgumentException("balanceAfter cannot be null");
    }
}
