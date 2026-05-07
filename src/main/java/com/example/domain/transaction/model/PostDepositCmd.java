package com.example.domain.transaction.model;

import com.example.domain.shared.Command;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Command to credit funds to a specific account.
 * Implements the Command marker interface from the shared domain.
 */
public record PostDepositCmd(String transactionId, String accountNumber, BigDecimal amount, String currency) implements Command {
    public PostDepositCmd {
        Objects.requireNonNull(transactionId, "transactionId cannot be null");
        Objects.requireNonNull(accountNumber, "accountNumber cannot be null");
        Objects.requireNonNull(amount, "amount cannot be null");
        Objects.requireNonNull(currency, "currency cannot be null");
    }
}