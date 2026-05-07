package com.example.domain;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

public class S11Event {
    private final UUID transactionId;
    private final String accountNumber;
    private final BigDecimal amount;
    private final Currency currency;
    private final BigDecimal resultingBalance;

    public S11Event(UUID transactionId, String accountNumber, BigDecimal amount, Currency currency, BigDecimal resultingBalance) {
        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.currency = currency;
        this.resultingBalance = resultingBalance;
    }

    public String getType() {
        return "withdrawal.posted";
    }

    public UUID getTransactionId() {
        return transactionId;
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
