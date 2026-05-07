package com.example.domain.transfer.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a transfer is initiated.
 * Story: S-13
 */
public class TransferInitiatedEvent implements DomainEvent {
    private final String eventId;
    private final String transferId;
    private final String fromAccountId;
    private final String toAccountId;
    private final BigDecimal amount;
    private final String currency;
    private final Instant occurredAt;

    public TransferInitiatedEvent(String transferId, String fromAccountId, String toAccountId, BigDecimal amount, String currency, Instant occurredAt) {
        this.eventId = UUID.randomUUID().toString();
        this.transferId = transferId;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
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
        return transferId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    public String eventId() { return eventId; }
    public String fromAccountId() { return fromAccountId; }
    public String toAccountId() { return toAccountId; }
    public BigDecimal amount() { return amount; }
    public String currency() { return currency; }
}
