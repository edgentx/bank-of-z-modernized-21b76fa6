package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record AccountClosedEvent(
    String aggregateId,
    String accountNumber,
    Instant occurredAt
) implements DomainEvent {
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
}