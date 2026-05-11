package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when an account is successfully closed.
 */
public record AccountClosedEvent(String accountNumber, Instant occurredAt) implements DomainEvent {
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
}