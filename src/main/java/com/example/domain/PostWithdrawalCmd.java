package com.example.domain;

import java.math.BigDecimal;
import java.util.Currency;

public record PostWithdrawalCmd(
    AccountNumber accountNumber,
    BigDecimal amount,
    String currencyCode
) {
    public PostWithdrawalCmd {
        if (accountNumber == null) throw new IllegalArgumentException("accountNumber required");
        if (amount == null) throw new IllegalArgumentException("amount required");
        if (currencyCode == null) throw new IllegalArgumentException("currencyCode required");
    }
}
