package com.example.domain;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

public class PostDepositCmd {
    private final UUID transactionId;
    private final String accountNumber;
    private final BigDecimal amount;
    private final Currency currency;

    public PostDepositCmd(UUID transactionId, String accountNumber, BigDecimal amount, Currency currency) {
        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.currency = currency;
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
