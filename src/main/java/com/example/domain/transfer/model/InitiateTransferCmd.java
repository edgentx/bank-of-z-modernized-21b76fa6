package com.example.domain.transfer.model;

import com.example.domain.shared.Command;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Command to initiate a new funds transfer.
 * Value object capturing the request parameters.
 */
public record InitiateTransferCmd(
        String transferId,
        String fromAccountId,
        String toAccountId,
        BigDecimal amount,
        String currency,
        BigDecimal availableBalance
) implements Command {
    public InitiateTransferCmd {
        Objects.requireNonNull(transferId, "transferId required");
        Objects.requireNonNull(fromAccountId, "fromAccountId required");
        Objects.requireNonNull(toAccountId, "toAccountId required");
        Objects.requireNonNull(amount, "amount required");
        Objects.requireNonNull(currency, "currency required");
        Objects.requireNonNull(availableBalance, "availableBalance required");
        if (currency.length() != 3) throw new IllegalArgumentException("currency must be 3-letter ISO code");
    }
}
