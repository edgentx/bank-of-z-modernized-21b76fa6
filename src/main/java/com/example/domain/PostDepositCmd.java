package com.example.domain;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

/**
 * Command to post a deposit to a transaction aggregate.
 * Uses Java Record for immutability.
 */
public record PostDepositCmd(
    UUID transactionId,
    String accountNumber,
    BigDecimal amount,
    Currency currency
) {
    public PostDepositCmd {
        if (transactionId == null) throw new IllegalArgumentException("transactionId cannot be null");
        if (accountNumber == null || accountNumber.isBlank()) throw new IllegalArgumentException("accountNumber cannot be blank");
        if (amount == null) throw new IllegalArgumentException("amount cannot be null");
        if (currency == null) throw new IllegalArgumentException("currency cannot be null");
    }
}
