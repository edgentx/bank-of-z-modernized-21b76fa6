package com.example.domain;

import java.util.UUID;

public class DepositPostedEvent {
    private final UUID transactionId;
    private final String accountNumber;
    private final Money amount;

    public DepositPostedEvent(UUID transactionId, String accountNumber, Money amount) {
        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.amount = amount;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public Money getAmount() {
        return amount;
    }
}
