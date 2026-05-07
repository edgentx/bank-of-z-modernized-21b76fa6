package com.example.domain.transaction.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionReversedEvent(
    String aggregateId,
    String originalTransactionId,
    BigDecimal amount,
    String accountId,
    Instant occurredAt
) implements DomainEvent {

    public TransactionReversedEvent {
        // Validation if necessary
    }

    @Override
    public String type() {
        return "transaction.reversed";
    }

    // Ensure we match the interface strictly
    public String aggregateId() {
        return aggregateId;
    }
}
