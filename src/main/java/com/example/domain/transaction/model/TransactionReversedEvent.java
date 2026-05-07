package com.example.domain.transaction.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionReversedEvent(
    String aggregateId,
    String originalTransactionId,
    BigDecimal reversalAmount,
    Instant occurredAt
) implements DomainEvent {

    public TransactionReversedEvent {
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId cannot be null");
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
}