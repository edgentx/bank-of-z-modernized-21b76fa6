package com.example.domain.account.model;

import com.example.domain.shared.Command;

import java.math.BigDecimal;
import java.util.Objects;

public record OpenAccountCmd(
    String accountId,
    String customerId,
    String accountType,
    BigDecimal initialDeposit,
    String sortCode
) implements Command {
    public OpenAccountCmd {
        Objects.requireNonNull(accountId, "accountId required");
        Objects.requireNonNull(customerId, "customerId required");
        Objects.requireNonNull(accountType, "accountType required");
        Objects.requireNonNull(initialDeposit, "initialDeposit required");
        Objects.requireNonNull(sortCode, "sortCode required");
    }
}
