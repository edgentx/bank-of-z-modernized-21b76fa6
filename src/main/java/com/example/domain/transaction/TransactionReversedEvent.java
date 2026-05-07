package com.example.domain.transaction;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a transaction is successfully reversed.
 */
public class TransactionReversedEvent implements DomainEvent {
    
    private final String eventId = UUID.randomUUID().toString();
    private final String aggregateId;
    private final BigDecimal reversalAmount;
    private final Instant occurredAt;

    public TransactionReversedEvent(String aggregateId, BigDecimal reversalAmount, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.reversalAmount = reversalAmount;
        this.occurredAt = occurredAt;
    }

    @Override
    public String type() {
        return "transaction.reversed";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    public BigDecimal reversalAmount() {
        return reversalAmount;
    }

    public String eventId() {
        return eventId;
    }
}