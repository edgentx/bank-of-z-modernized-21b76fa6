package com.example.domain.transfer.model;

import com.example.domain.shared.Command;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Command to initiate a transfer of funds between two accounts.
 */
public record InitiateTransferCmd(
        String transferId,
        String fromAccountId,
        String toAccountId,
        BigDecimal amount,
        String currency
) implements Command {
    public InitiateTransferCmd {
        Objects.requireNonNull(transferId, "transferId cannot be null");
        Objects.requireNonNull(fromAccountId, "fromAccountId cannot be null");
        Objects.requireNonNull(toAccountId, "toAccountId cannot be null");
        Objects.requireNonNull(amount, "amount cannot be null");
        Objects.requireNonNull(currency, "currency cannot be null");
    }
}
