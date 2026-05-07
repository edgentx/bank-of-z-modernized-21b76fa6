package com.example.domain.transaction.model;

import com.example.domain.shared.Command;

import java.math.BigDecimal;
import java.util.Objects;

public record PostWithdrawalCmd(String accountNumber, BigDecimal amount, String currency) implements Command {
    public PostWithdrawalCmd {
        Objects.requireNonNull(accountNumber, "accountNumber cannot be null");
        Objects.requireNonNull(amount, "amount cannot be null");
        Objects.requireNonNull(currency, "currency cannot be null");
        if (amount.scale() > 2) {
            throw new IllegalArgumentException("Amount scale cannot exceed 2");
        }
    }
}