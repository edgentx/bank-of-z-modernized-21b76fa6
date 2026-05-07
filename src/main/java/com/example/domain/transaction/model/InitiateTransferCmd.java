package com.example.domain.transaction.model;

import com.example.domain.shared.Command;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Command to initiate a transfer of funds between two accounts.
 * Part of S-13: Implement InitiateTransferCmd on Transfer.
 */
public record InitiateTransferCmd(
        String transferId,
        String fromAccountId,
        String toAccountId,
        BigDecimal amount,
        String currency,
        BigDecimal availableBalance // Snapshot of balance at time of command for validation
) implements Command {

    public InitiateTransferCmd {
        Objects.requireNonNull(transferId, "transferId cannot be null");
        Objects.requireNonNull(fromAccountId, "fromAccountId cannot be null");
        Objects.requireNonNull(toAccountId, "toAccountId cannot be null");
        Objects.requireNonNull(amount, "amount cannot be null");
        Objects.requireNonNull(currency, "currency cannot be null");
        Objects.requireNonNull(availableBalance, "availableBalance cannot be null");
        if (amount.signum() <= 0) throw new IllegalArgumentException("amount must be positive");
    }
}
