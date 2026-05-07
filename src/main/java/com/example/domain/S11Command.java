package com.example.domain;

import java.math.BigDecimal;
import java.util.UUID;

public record S11Command(
        UUID transactionId,
        String accountNumber,
        BigDecimal amount,
        String currency,
        BigDecimal currentBalance
) {
    public S11Command {
        if (accountNumber == null) {
            throw new IllegalArgumentException("accountNumber cannot be null");
        }
        if (amount == null) {
            throw new IllegalArgumentException("amount cannot be null");
        }
        if (currency == null) {
            throw new IllegalArgumentException("currency cannot be null");
        }
    }
}
