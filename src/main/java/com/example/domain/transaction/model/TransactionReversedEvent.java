package com.example.domain.transaction.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionReversedEvent(
        String aggregateId,
        String originalTransactionId,
        BigDecimal amount,
        String accountNumber,
        Instant occurredAt
) implements DomainEvent {

    public TransactionReversedEvent {
        // Validating record components if needed, though constructor is implicit
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
