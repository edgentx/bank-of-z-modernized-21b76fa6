package com.example.domain;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Command to credit funds to a specific account.
 * Part of S-10: Implement PostDepositCmd.
 */
public record PostDepositCmd(UUID transactionId, String accountNumber, BigDecimal amount, String currency) {
    public PostDepositCmd {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Account number cannot be null or blank");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency cannot be null or blank");
        }
    }
}
