package com.example.domain.transaction.event;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionReversedEvent(
        String eventId,
        String aggregateId,
        String originalTransactionId,
        BigDecimal reversalAmount,
        Instant occurredAt
) implements DomainEvent {
    public TransactionReversedEvent(String aggregateId, String originalTransactionId, BigDecimal reversalAmount) {
        this(UUID.randomUUID().toString(), aggregateId, originalTransactionId, reversalAmount, Instant.now());
    }

    @Override
    public String type() {
        return "transaction.reversed";
    }
}
