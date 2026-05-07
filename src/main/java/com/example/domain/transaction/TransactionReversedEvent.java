package com.example.domain.transaction;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record TransactionReversedEvent(
    String aggregateId,
    String originalTransactionId,
    String amount,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "transaction.reversed";
    }
}
