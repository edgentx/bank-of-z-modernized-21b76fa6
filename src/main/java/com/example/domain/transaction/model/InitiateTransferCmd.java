package com.example.domain.transaction.model;

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
        String currency,
        BigDecimal sourceAvailableBalance
) implements Command {

    public InitiateTransferCmd {
        if (transferId == null || transferId.isBlank()) {
            throw new IllegalArgumentException("transferId cannot be null or empty");
        }
        if (fromAccountId == null || fromAccountId.isBlank()) {
            throw new IllegalArgumentException("fromAccountId cannot be null or empty");
        }
        if (toAccountId == null || toAccountId.isBlank()) {
            throw new IllegalArgumentException("toAccountId cannot be null or empty");
        }
        if (amount == null) {
            throw new IllegalArgumentException("amount cannot be null");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("currency cannot be null or empty");
        }
        Objects.requireNonNull(sourceAvailableBalance, "sourceAvailableBalance cannot be null");
    }
}
