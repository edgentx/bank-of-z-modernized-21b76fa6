package com.example.domain;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;
import java.util.UUID;

/**
 * Event representing a Posted Withdrawal.
 * Part of Story S-11.
 */
public class S11Event {

    private final UUID transactionId;
    private final String type;
    private final String accountNumber;
    private final BigDecimal amount;
    private final Currency currency;
    private final long timestamp;

    public S11Event(UUID transactionId, String accountNumber, BigDecimal amount, Currency currency) {
        this.transactionId = transactionId;
        this.type = "withdrawal.posted";
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.currency = currency;
        this.timestamp = System.currentTimeMillis();
    }

    public UUID getTransactionId() {
        return transactionId;
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

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        S11Event s11Event = (S11Event) o;
        return Objects.equals(transactionId, s11Event.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }
}
