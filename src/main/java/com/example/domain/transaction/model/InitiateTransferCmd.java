package com.example.domain.transaction.model;

import com.example.domain.shared.Command;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Command to initiate a transfer of funds between two accounts.
 * S-13: Implement InitiateTransferCmd on Transfer (transaction-processing).
 */
public record InitiateTransferCmd(
        String transferId,
        String fromAccountId,
        String toAccountId,
        BigDecimal amount,
        String currency,
        BigDecimal availableBalance // Snapshot provided to enforce invariant at command time
) implements Command {

    public InitiateTransferCmd {
        Objects.requireNonNull(transferId, "transferId required");
        Objects.requireNonNull(fromAccountId, "fromAccountId required");
        Objects.requireNonNull(toAccountId, "toAccountId required");
        Objects.requireNonNull(amount, "amount required");
        Objects.requireNonNull(currency, "currency required");
        Objects.requireNonNull(availableBalance, "availableBalance required");

        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
    }
}
