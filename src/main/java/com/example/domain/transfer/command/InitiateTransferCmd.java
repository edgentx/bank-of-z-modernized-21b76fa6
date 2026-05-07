package com.example.domain.transfer.command;

import com.example.domain.shared.Command;

import java.math.BigDecimal;
import java.util.Objects;

public record InitiateTransferCmd(
    String transferId,
    String fromAccount,
    String toAccount,
    BigDecimal amount,
    BigDecimal availableBalance // Provided to check the invariant within the aggregate context or externally
) implements Command {
    public InitiateTransferCmd {
        Objects.requireNonNull(transferId);
        Objects.requireNonNull(fromAccount);
        Objects.requireNonNull(toAccount);
        Objects.requireNonNull(amount);
    }
}
