package com.example.domain.transaction.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a transfer is completed.
 */
public record TransferCompletedEvent(
    String aggregateId,
    String sourceAccount,
    String destinationAccount,
    BigDecimal amount,
    String currency,
    Instant occurredAt
) implements DomainEvent {
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
}
