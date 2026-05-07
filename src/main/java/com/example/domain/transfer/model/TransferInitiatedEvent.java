package com.example.domain.transfer.model;

import com.example.domain.shared.DomainEvent;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class TransferInitiatedEvent implements DomainEvent {
    private final String eventId;
    private final String aggregateId;
    private final String fromAccount;
    private final String toAccount;
    private final BigDecimal amount;
    private final String currency;
    private final Instant occurredAt;

    public TransferInitiatedEvent(String aggregateId, String fromAccount, String toAccount, BigDecimal amount, String currency, Instant occurredAt) {
        this.eventId = UUID.randomUUID().toString();
        this.aggregateId = aggregateId;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.currency = currency;
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

    public String fromAccount() { return fromAccount; }
    public String toAccount() { return toAccount; }
    public BigDecimal amount() { return amount; }
    public String currency() { return currency; }
}
