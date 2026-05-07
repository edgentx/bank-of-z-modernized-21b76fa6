package com.example.domain.transfer.model;

import com.example.domain.shared.Command;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Command to initiate a transfer of funds between two accounts.
 * S-13 Implementation.
 */
public record InitiateTransferCmd(
    String transferId,
    String fromAccount,
    String toAccount,
    BigDecimal amount
) implements Command {

    public InitiateTransferCmd {
        Objects.requireNonNull(transferId, "transferId required");
        Objects.requireNonNull(fromAccount, "fromAccount required");
        Objects.requireNonNull(toAccount, "toAccount required");
        Objects.requireNonNull(amount, "amount required");
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
    }
}
