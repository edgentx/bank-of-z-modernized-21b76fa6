package com.example.domain.transaction.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class TransferCompletedEvent implements DomainEvent {
    private final String eventId;
    private final String aggregateId;
    private final String transferId;
    private final String sourceAccountId;
    private final String destinationAccountId;
    private final BigDecimal amount;
    private final Instant occurredAt;

    public TransferCompletedEvent(String aggregateId, String transferId, String sourceAccountId, String destinationAccountId, BigDecimal amount, Instant occurredAt) {
        this.eventId = UUID.randomUUID().toString();
        this.aggregateId = aggregateId;
        this.transferId = transferId;
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.amount = amount;
        this.occurredAt = occurredAt;
    }

    @Override
    public String type() {
        return "transfer.completed";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    public String getTransferId() { return transferId; }
    public String getSourceAccountId() { return sourceAccountId; }
    public String getDestinationAccountId() { return destinationAccountId; }
    public BigDecimal getAmount() { return amount; }
}
