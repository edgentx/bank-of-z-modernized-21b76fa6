package com.example.domain.transfer;

import com.example.domain.shared.Command;
import java.math.BigDecimal;
import java.util.UUID;

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
        if (transferId == null || transferId.isBlank()) {
            throw new IllegalArgumentException("transferId cannot be null or blank");
        }
        if (fromAccountId == null || fromAccountId.isBlank()) {
            throw new IllegalArgumentException("fromAccountId cannot be null or blank");
        }
        if (toAccountId == null || toAccountId.isBlank()) {
            throw new IllegalArgumentException("toAccountId cannot be null or blank");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        if (currency == null || currency.length() != 3) {
            throw new IllegalArgumentException("currency must be a valid ISO 4217 code");
        }
    }
}
