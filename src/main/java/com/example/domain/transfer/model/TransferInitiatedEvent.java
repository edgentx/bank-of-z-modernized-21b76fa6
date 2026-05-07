package com.example.domain.transfer.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class TransferInitiatedEvent implements DomainEvent {
    private final String eventId;
    private final String aggregateId;
    private final String fromAccountId;
    private final String toAccountId;
    private final BigDecimal amount;
    private final Instant occurredAt;

    public TransferInitiatedEvent(String aggregateId, String fromAccountId, String toAccountId, BigDecimal amount, Instant occurredAt) {
        this.eventId = UUID.randomUUID().toString();
        this.aggregateId = aggregateId;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.occurredAt = occurredAt;
    }

    @Override
    public String type() {
        return "transfer.initiated";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    public String fromAccountId() { return fromAccountId; }
    public String toAccountId() { return toAccountId; }
    public BigDecimal amount() { return amount; }
}
