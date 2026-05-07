package com.example.domain;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Event representing a posted withdrawal (S-11).
 */
public class S11Event {
    private final String eventId; // UUID or similar
    private final String accountNumber;
    private final BigDecimal amount;
    private final String currency;
    private final TransactionStatus status;

    public S11Event(String eventId, String accountNumber, BigDecimal amount, String currency, TransactionStatus status) {
        this.eventId = eventId;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
    }

    public String getEventId() {
        return eventId;
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

    public TransactionStatus getStatus() {
        return status;
    }
}
