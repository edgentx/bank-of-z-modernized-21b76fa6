package com.example.domain;

import java.util.Currency;
import java.math.BigDecimal;

/**
 * Command to post a deposit to a transaction.
 * Records: S-10
 */
public record PostDepositCmd(
        String transactionId,
        String accountNumber,
        BigDecimal amount,
        Currency currency
) {
    public PostDepositCmd {
        if (transactionId == null || transactionId.isBlank()) {
            throw new IllegalArgumentException("Transaction ID cannot be null or blank");
        }
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Account Number cannot be null or blank");
        }
        // Note: Invariant checks (amount > 0) are handled by the Aggregate logic,
        // but basic structural integrity can be validated here if desired.
    }
}
