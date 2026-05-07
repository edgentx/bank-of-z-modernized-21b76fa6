package com.example.domain;

import java.math.BigDecimal;
import java.util.Currency;

public class WithdrawalPosted {
    private final String accountNumber;
    private final BigDecimal amount;
    private final Currency currency;
    private final String transactionId;

    public WithdrawalPosted(String accountNumber, BigDecimal amount, Currency currency, String transactionId) {
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.currency = currency;
        this.transactionId = transactionId;
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

    public String getTransactionId() {
        return transactionId;
    }
}
