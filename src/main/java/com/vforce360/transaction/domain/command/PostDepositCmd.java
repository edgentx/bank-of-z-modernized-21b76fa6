package com.vforce360.transaction.domain.command;

import java.math.BigDecimal;
import java.util.UUID;

public class PostDepositCmd {
    private final UUID transactionId;
    private final String accountNumber;
    private final BigDecimal amount;
    private final String currency;

    public PostDepositCmd(UUID transactionId, String accountNumber, BigDecimal amount, String currency) {
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

    public String getCurrency() {
        return currency;
    }
}
