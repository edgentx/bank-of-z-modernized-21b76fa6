package com.example.domain;

import java.math.BigDecimal;
import java.util.Objects;

public record PostDepositCmd(AccountNumber accountNumber, Money amount) {
    public PostDepositCmd {
        Objects.requireNonNull(accountNumber, "Account number is required");
        Objects.requireNonNull(amount, "Amount is required");
    }
}
