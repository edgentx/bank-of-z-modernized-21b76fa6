package com.example.domain.transfer.model;

import com.example.domain.shared.Command;

import java.math.BigDecimal;
import java.util.Objects;

public record InitiateTransferCmd(
        String transferId,
        String fromAccountId,
        String toAccountId,
        BigDecimal amount
) implements Command {
    public InitiateTransferCmd {
        Objects.requireNonNull(transferId, "transferId cannot be null");
        // Basic validation at the command boundary
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }
}
