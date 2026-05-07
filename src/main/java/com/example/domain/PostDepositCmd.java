package com.example.domain;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

public record PostDepositCmd(String accountNumber, BigDecimal amount, Currency currency) {
    public PostDepositCmd {
        Objects.requireNonNull(accountNumber, "Account number cannot be null");
        Objects.requireNonNull(amount, "Amount cannot be null");
        Objects.requireNonNull(currency, "Currency cannot be null");
    }
}
