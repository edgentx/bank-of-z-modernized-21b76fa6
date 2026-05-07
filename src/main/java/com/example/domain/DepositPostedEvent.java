package com.example.domain;

import java.math.BigDecimal;
import java.util.UUID;

public class DepositPostedEvent implements S10Event {
    private final UUID transactionId;
    private final BigDecimal amount;
    private final String currency;
    private final String accountNumber;

    public DepositPostedEvent(UUID transactionId, BigDecimal amount, String currency, String accountNumber) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.currency = currency;
        this.accountNumber = accountNumber;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}
