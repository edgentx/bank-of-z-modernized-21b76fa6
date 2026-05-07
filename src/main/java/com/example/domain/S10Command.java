package com.example.domain;

import java.math.BigDecimal;
import java.util.UUID;

public record PostDepositCmd(TransactionId transactionId, String accountNumber, BigDecimal amount, String currency) {
    public PostDepositCmd {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Account number cannot be null");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency cannot be null");
        }
    }
}
