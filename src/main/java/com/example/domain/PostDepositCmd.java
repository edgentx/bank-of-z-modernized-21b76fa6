package com.example.domain;

import java.math.BigDecimal;

public class PostDepositCmd {
    private final String accountNumber;
    private final BigDecimal amount;
    private final String currency;

    public PostDepositCmd(String accountNumber, BigDecimal amount, String currency) {
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.currency = currency;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }
}
