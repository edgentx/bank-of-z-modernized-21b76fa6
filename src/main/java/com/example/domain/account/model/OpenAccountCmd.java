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
        Objects.requireNonNull(accountId, "accountId cannot be null");
        Objects.requireNonNull(customerId, "customerId cannot be null");
        Objects.requireNonNull(accountType, "accountType cannot be null");
        Objects.requireNonNull(initialDeposit, "initialDeposit cannot be null");
        Objects.requireNonNull(sortCode, "sortCode cannot be null");

        if (accountId.isBlank()) throw new IllegalArgumentException("accountId cannot be blank");
        if (customerId.isBlank()) throw new IllegalArgumentException("customerId cannot be blank");
        if (accountType.isBlank()) throw new IllegalArgumentException("accountType cannot be blank");
        if (sortCode.isBlank()) throw new IllegalArgumentException("sortCode cannot be blank");
        if (initialDeposit.signum() < 0) throw new IllegalArgumentException("initialDeposit cannot be negative");
    }
}
