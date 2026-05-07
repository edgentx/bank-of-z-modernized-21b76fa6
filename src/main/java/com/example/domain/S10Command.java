package com.example.domain;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Command for S-10: PostDepositCmd.
 * Immutable record representing the request to credit funds.
 */
public record PostDepositCmd(
        UUID transactionId,
        String accountNumber,
        BigDecimal amount,
        String currency
) {
    public PostDepositCmd {
        if (transactionId == null) throw new IllegalArgumentException("transactionId cannot be null");
        if (accountNumber == null || accountNumber.isBlank()) throw new IllegalArgumentException("accountNumber cannot be blank");
        if (amount == null) throw new IllegalArgumentException("amount cannot be null");
        if (currency == null || currency.isBlank()) throw new IllegalArgumentException("currency cannot be blank");
    }
}
