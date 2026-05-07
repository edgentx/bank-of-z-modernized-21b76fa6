package com.example.domain;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

public record PostWithdrawalCmd(String accountNumber, BigDecimal amount, Currency currency) {
    public PostWithdrawalCmd {
        Objects.requireNonNull(accountNumber, "accountNumber cannot be null");
        Objects.requireNonNull(amount, "amount cannot be null");
        Objects.requireNonNull(currency, "currency cannot be null");
    }
}
