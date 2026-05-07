package com.example.domain;

import java.math.BigDecimal;
import java.util.Currency;
import java.time.LocalDateTime;

/**
 * Domain Event emitted when a deposit is successfully posted.
 */
public class DepositPostedEvent implements DomainEvent {

    private final String transactionId;
    private final String accountNumber;
    private final BigDecimal amount;
    private final Currency currency;
    private final LocalDateTime occurredAt;

    public DepositPostedEvent(String transactionId, String accountNumber, BigDecimal amount, Currency currency) {
        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.currency = currency;
        this.occurredAt = LocalDateTime.now();
    }

    public String getTransactionId() {
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

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }
}