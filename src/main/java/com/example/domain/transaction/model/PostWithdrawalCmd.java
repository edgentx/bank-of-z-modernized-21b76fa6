package com.example.domain.transaction.model;

import com.example.domain.shared.Command;

import java.math.BigDecimal;
import java.util.Objects;

public record PostWithdrawalCmd(String accountNumber, BigDecimal amount, String currency) implements Command {
    public PostWithdrawalCmd {
        Objects.requireNonNull(accountNumber, "accountNumber required");
        Objects.requireNonNull(amount, "amount required");
        Objects.requireNonNull(currency, "currency required");
        if (amount.scale() > 2) throw new IllegalArgumentException("amount scale cannot exceed 2");
    }
}
