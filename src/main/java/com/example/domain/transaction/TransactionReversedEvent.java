package com.example.domain.transaction;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionReversedEvent(
        String eventId,
        String originalTransactionId,
        String reversingTransactionId,
        BigDecimal amount,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "transaction.reversed";
    }

    @Override
    public String aggregateId() {
        return reversingTransactionId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
