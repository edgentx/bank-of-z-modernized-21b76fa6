package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class AccountStatusUpdatedEvent implements DomainEvent {
    private final String eventId = UUID.randomUUID().toString();
    private final String aggregateId;
    private final String accountNumber;
    private final String oldStatus;
    private final String newStatus;
    private final Instant occurredAt;

    public AccountStatusUpdatedEvent(String aggregateId, String accountNumber, String oldStatus, String newStatus, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.accountNumber = accountNumber;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.occurredAt = occurredAt;
    }

    @Override
    public String type() {
        return "account.status.updated";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    public String getAccountNumber() { return accountNumber; }
    public String getOldStatus() { return oldStatus; }
    public String getNewStatus() { return newStatus; }
}