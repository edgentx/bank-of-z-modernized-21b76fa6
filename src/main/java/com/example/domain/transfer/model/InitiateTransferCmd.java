package com.example.domain.transfer.model;

import com.example.domain.shared.Command;
import java.math.BigDecimal;
import java.util.Objects;

public record InitiateTransferCmd(
    String transferId,
    String fromAccount,
    String toAccount,
    BigDecimal amount,
    String currency,
    BigDecimal availableBalance
) implements Command {
    public InitiateTransferCmd {
        Objects.requireNonNull(transferId);
        Objects.requireNonNull(fromAccount);
        Objects.requireNonNull(toAccount);
        Objects.requireNonNull(amount);
        Objects.requireNonNull(currency);
        Objects.requireNonNull(availableBalance);
    }
}
