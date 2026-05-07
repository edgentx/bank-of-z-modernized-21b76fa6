package com.example.domain;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

public class PostWithdrawalCmd {

    private final UUID transactionId;
    private final BigDecimal amount;
    private final Currency currency;
    private final String accountNumber;

    // Constructor matching the 4-arg requirement from review feedback
    public PostWithdrawalCmd(UUID transactionId, BigDecimal amount, Currency currency, String accountNumber) {
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

    public Currency getCurrency() {
        return currency;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}
