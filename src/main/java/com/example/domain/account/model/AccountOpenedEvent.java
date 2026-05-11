package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class AccountOpenedEvent implements DomainEvent {
    private final String aggregateId;
    private final String customerId;
    private final String accountType;
    private final BigDecimal initialBalance;
    private final String sortCode;
    private final String accountNumber;
    private final Instant occurredAt;

    public AccountOpenedEvent(String aggregateId, String customerId, String accountType, BigDecimal initialBalance, String sortCode, String accountNumber, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.customerId = customerId;
        this.accountType = accountType;
        this.initialBalance = initialBalance;
        this.sortCode = sortCode;
        this.accountNumber = accountNumber;
        this.occurredAt = occurredAt;
    }

    @Override
    public String type() {
        return "account.opened";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    // Getters
    public String getCustomerId() { return customerId; }
    public String getAccountType() { return accountType; }
    public BigDecimal getInitialBalance() { return initialBalance; }
    public String getSortCode() { return sortCode; }
    public String getAccountNumber() { return accountNumber; }
}
