package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public class AccountStatusUpdatedEvent implements DomainEvent {
    private final String eventId = UUID.randomUUID().toString();
    private final String aggregateId;
    private final String accountNumber;
    private final AccountAggregate.AccountStatus oldStatus;
    private final AccountAggregate.AccountStatus newStatus;
    private final Instant occurredAt;

    public AccountStatusUpdatedEvent(String aggregateId, String accountNumber, AccountAggregate.AccountStatus oldStatus, AccountAggregate.AccountStatus newStatus, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.accountNumber = accountNumber;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.occurredAt = occurredAt;
    }

    @Override public String type() { return "account.status.updated"; }
    @Override public String aggregateId() { return aggregateId; }
    @Override public Instant occurredAt() { return occurredAt; }
    
    public String getAccountNumber() { return accountNumber; }
    public AccountAggregate.AccountStatus getOldStatus() { return oldStatus; }
    public AccountAggregate.AccountStatus getNewStatus() { return newStatus; }
}
