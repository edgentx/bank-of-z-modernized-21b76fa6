package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class AccountOpenedEvent implements DomainEvent {
    private final String eventId;
    private final String aggregateId;
    private final String customerId;
    private final String accountNumber; // Business Key
    private final AccountAggregate.AccountType accountType;
    private final BigDecimal balance;
    private final String sortCode;
    private final Instant occurredAt;

    public AccountOpenedEvent(String aggregateId, String customerId, String accountNumber, 
                              AccountAggregate.AccountType accountType, BigDecimal balance, String sortCode, Instant occurredAt) {
        this.eventId = UUID.randomUUID().toString();
        this.aggregateId = aggregateId;
        this.customerId = customerId;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
        this.sortCode = sortCode;
        this.occurredAt = occurredAt;
    }

    @Override public String type() { return "account.opened"; }
    @Override public String aggregateId() { return aggregateId; }
    @Override public Instant occurredAt() { return occurredAt; }
    
    public String getCustomerId() { return customerId; }
    public String getAccountNumber() { return accountNumber; }
    public AccountAggregate.AccountType getAccountType() { return accountType; }
    public BigDecimal getBalance() { return balance; }
    public String getSortCode() { return sortCode; }
}