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
        String fromAccount,
        String toAccount,
        BigDecimal amount,
        String currency,
        BigDecimal availableBalance // Snapshot passed for validation within aggregate context
) implements Command {

    public InitiateTransferCmd {
        Objects.requireNonNull(transferId, "transferId cannot be null");
        Objects.requireNonNull(fromAccount, "fromAccount cannot be null");
        Objects.requireNonNull(toAccount, "toAccount cannot be null");
        Objects.requireNonNull(amount, "amount cannot be null");
        Objects.requireNonNull(currency, "currency cannot be null");
        Objects.requireNonNull(availableBalance, "availableBalance cannot be null");
    }
}