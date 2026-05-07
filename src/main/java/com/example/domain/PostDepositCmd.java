package com.example.domain;

import java.math.BigDecimal;

public record PostDepositCmd(String accountNumber, BigDecimal amount, String currency) {
    public PostDepositCmd {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("AccountNumber cannot be null or empty");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency cannot be null or empty");
        }
    }
}