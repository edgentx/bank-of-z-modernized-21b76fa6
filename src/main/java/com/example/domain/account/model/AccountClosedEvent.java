package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class AccountClosedEvent implements DomainEvent {
    private final String eventId;
    private final String accountNumber;
    private final Instant occurredAt;

    public AccountClosedEvent(String accountNumber, Instant occurredAt) {
        this.eventId = UUID.randomUUID().toString();
        this.accountNumber = accountNumber;
        this.occurredAt = occurredAt;
    }

    @Override
    public String type() {
        return "account.closed";
    }

    @Override
    public String aggregateId() {
        return accountNumber;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    public String accountNumber() {
        return accountNumber;
    }
}