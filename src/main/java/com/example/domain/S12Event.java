package com.example.domain;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record TransactionReversedEvent(
    String aggregateId,
    String originalTransactionId,
    double amount,
    Instant occurredAt
) implements DomainEvent {
    public TransactionReversedEvent {
        // Validating amount is positive as per invariants
        if (amount <= 0) throw new IllegalArgumentException("Amount must be greater than zero");
    }

    public TransactionReversedEvent(String originalTransactionId, double amount) {
        this(UUID.randomUUID().toString(), originalTransactionId, amount, Instant.now());
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
