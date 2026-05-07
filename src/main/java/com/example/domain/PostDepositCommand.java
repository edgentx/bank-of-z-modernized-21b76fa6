package com.example.domain;

import java.math.BigDecimal;
import java.util.Currency;

public record PostDepositCommand(String accountNumber, BigDecimal amount, Currency currency) {
    public PostDepositCommand {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Account number cannot be null or empty");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (currency == null) {
            throw new IllegalArgumentException("Currency cannot be null");
        }
    }
}
