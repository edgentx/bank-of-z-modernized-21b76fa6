package com.example.domain.transaction.model;

import com.example.domain.shared.Command;

import java.math.BigDecimal;
import java.util.Objects;

public record CompleteTransferCmd(
        String transferId,
        String sourceAccountId,
        String destinationAccountId,
        BigDecimal amount,
        BigDecimal sourceAvailableBalance,
        boolean atomicState
) implements Command {

    public CompleteTransferCmd {
        Objects.requireNonNull(transferId, "transferId cannot be null");
        Objects.requireNonNull(sourceAccountId, "sourceAccountId cannot be null");
        Objects.requireNonNull(destinationAccountId, "destinationAccountId cannot be null");
        Objects.requireNonNull(amount, "amount cannot be null");
        // Balance can be zero, but not null
        Objects.requireNonNull(sourceAvailableBalance, "sourceAvailableBalance cannot be null");
    }
}
