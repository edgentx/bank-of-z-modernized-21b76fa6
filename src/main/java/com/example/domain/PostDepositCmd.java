package com.example.domain;

import java.math.BigDecimal;
import java.util.UUID;

public record PostDepositCmd(UUID transactionId, String accountNumber, BigDecimal amount, String currency) {
    public PostDepositCmd {
        if (amount == null) throw new IllegalArgumentException("Amount cannot be null");
        if (accountNumber == null) throw new IllegalArgumentException("Account Number cannot be null");
        if (currency == null) throw new IllegalArgumentException("Currency cannot be null");
    }
}