package com.example.domain.transfer.model;

import com.example.domain.shared.Command;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Command to initiate a transfer between two accounts.
 */
public record InitiateTransferCmd(
    String transferId,
    String fromAccountId,
    String toAccountId,
    BigDecimal amount,
    String currency
) implements Command {
    public InitiateTransferCmd {
        Objects.requireNonNull(transferId, "transferId required");
        Objects.requireNonNull(fromAccountId, "fromAccountId required");
        Objects.requireNonNull(toAccountId, "toAccountId required");
        Objects.requireNonNull(amount, "amount required");
        Objects.requireNonNull(currency, "currency required");
        if (amount.signum() <= 0) throw new IllegalArgumentException("amount must be positive");
        if (currency.length() != 3) throw new IllegalArgumentException("currency must be ISO 4217");
    }
}
