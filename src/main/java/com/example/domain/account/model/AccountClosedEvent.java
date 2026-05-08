package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class AccountClosedEvent implements DomainEvent {
    private final String eventId = UUID.randomUUID().toString();
    private final String aggregateId;
    private final String accountNumber;
    private final Instant occurredAt;

    public AccountClosedEvent(String aggregateId, String accountNumber, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.accountNumber = accountNumber;
        this.occurredAt = occurredAt;
    }

    @Override
    public String type() {
        return "account.closed";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}
