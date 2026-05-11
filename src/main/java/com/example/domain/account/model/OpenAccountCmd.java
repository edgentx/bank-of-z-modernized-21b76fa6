package com.example.domain.account.model;

import com.example.domain.shared.Command;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Command to open a new bank account.
 */
public record OpenAccountCmd(
    String commandId,
    String customerId,
    String accountType,
    BigDecimal initialDeposit,
    String sortCode
) implements Command {

    public OpenAccountCmd {
        Objects.requireNonNull(commandId, "commandId required");
        Objects.requireNonNull(customerId, "customerId required");
        Objects.requireNonNull(accountType, "accountType required");
        Objects.requireNonNull(initialDeposit, "initialDeposit required");
        Objects.requireNonNull(sortCode, "sortCode required");
        if (initialDeposit.signum() < 0) throw new IllegalArgumentException("initialDeposit must be non-negative");
    }
}
