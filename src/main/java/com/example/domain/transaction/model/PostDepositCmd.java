package com.example.domain.transaction.model;

import com.example.domain.shared.Command;
import java.math.BigDecimal;
import java.util.Objects;

public record PostDepositCmd(String transactionId, String accountNumber, BigDecimal amount, String currency) implements Command {
    public PostDepositCmd {
        Objects.requireNonNull(transactionId);
        Objects.requireNonNull(accountNumber);
        Objects.requireNonNull(amount);
        Objects.requireNonNull(currency);
    }
}