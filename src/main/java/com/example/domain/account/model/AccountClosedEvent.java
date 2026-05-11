package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public class AccountClosedEvent implements DomainEvent {
    private final String aggregateId;
    private final Instant occurredAt;

    public AccountClosedEvent(String aggregateId, Instant occurredAt) {
        this.aggregateId = aggregateId;
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
}
