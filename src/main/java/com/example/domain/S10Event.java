package com.example.domain;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Event representing a posted deposit.
 */
public class S10Event {
    public enum Type {
        DEPOSIT_POSTED
    }

    private final Type type;
    private final UUID transactionId;
    private final String accountNumber;
    private final BigDecimal amount;
    private final String currency;

    public S10Event(UUID transactionId, String accountNumber, BigDecimal amount, String currency) {
        this.type = Type.DEPOSIT_POSTED;
        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.currency = currency;
    }

    public Type getType() {
        return type;
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