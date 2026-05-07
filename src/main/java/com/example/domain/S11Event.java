package com.example.domain;

import java.math.BigDecimal;
import java.util.Currency;

public class S11Event {
    private final String type;
    private final String accountNumber;
    private final BigDecimal amount;
    private final Currency currency;

    public S11Event(String type, String accountNumber, BigDecimal amount, Currency currency) {
        this.type = type;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.currency = currency;
    }

    public String getType() {
        return type;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }
}
