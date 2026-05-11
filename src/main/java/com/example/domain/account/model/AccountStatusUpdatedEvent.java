package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

public class AccountStatusUpdatedEvent implements DomainEvent {
    private final String accountNumber;
    private final AccountAggregate.AccountStatus newStatus;
    private final Instant occurredAt;

    public AccountStatusUpdatedEvent(String accountNumber, AccountAggregate.AccountStatus newStatus, Instant occurredAt) {
        this.accountNumber = accountNumber;
        this.newStatus = newStatus;
        this.occurredAt = occurredAt;
    }

    @Override
    public String type() {
        return "account.status.updated";
    }

    @Override
    public String aggregateId() {
        return accountNumber;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public AccountAggregate.AccountStatus getNewStatus() {
        return newStatus;
    }
}
