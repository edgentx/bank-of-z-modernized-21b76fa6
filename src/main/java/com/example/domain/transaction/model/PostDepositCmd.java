package com.example.domain.transaction.model;

import com.example.domain.shared.Command;
import java.math.BigDecimal;
import java.util.Objects;

public record PostDepositCmd(String accountNumber, BigDecimal amount, String currency) implements Command {
    public PostDepositCmd {
        Objects.requireNonNull(accountNumber, "accountNumber required");
        Objects.requireNonNull(amount, "amount required");
        Objects.requireNonNull(currency, "currency required");
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount must be greater than zero");
        }
        if (currency.isBlank()) {
            throw new IllegalArgumentException("currency required");
        }
    }
}
