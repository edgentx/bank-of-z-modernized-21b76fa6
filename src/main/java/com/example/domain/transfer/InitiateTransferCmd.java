package com.example.domain.transfer;

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
        Objects.requireNonNull(transferId);
        Objects.requireNonNull(fromAccountId);
        Objects.requireNonNull(toAccountId);
        Objects.requireNonNull(amount);
    }
}
