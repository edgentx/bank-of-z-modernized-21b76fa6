package com.example.domain.transaction.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionReversedEvent(
        String aggregateId,
        String originalTransactionId,
        BigDecimal reversalAmount,
        String currency,
        Instant occurredAt
) implements DomainEvent {
    public TransactionReversedEvent {
        // Ensure default values if needed, though constructor handles it
    }

    public TransactionReversedEvent(String aggregateId, String originalTransactionId, BigDecimal reversalAmount, String currency) {
        this(aggregateId, originalTransactionId, reversalAmount, currency, Instant.now());
    }

    @Override
    public String type() {
        return "transaction.reversed";
    }
}
