package com.example.domain.account.model;

import java.math.BigDecimal;

public enum AccountType {
    SAVINGS(BigDecimal.valueOf(100.00)),
    CHECKING(BigDecimal.valueOf(0.00)),
    INVESTMENT(BigDecimal.valueOf(1000.00));

    private final BigDecimal minBalance;

    AccountType(BigDecimal minBalance) {
        this.minBalance = minBalance;
    }

    public BigDecimal getMinBalance() {
        return minBalance;
    }
}
