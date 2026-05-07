package com.example.domain.transaction.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * Domain event emitted when a transfer is initiated.
 * Part of S-13: Implement InitiateTransferCmd on Transfer.
 */
public record TransferInitiatedEvent(
        String aggregateId,
        String fromAccountId,
        String toAccountId,
        BigDecimal amount,
        String currency,
        Instant occurredAt
) implements DomainEvent {

    public TransferInitiatedEvent {
        Objects.requireNonNull(aggregateId, "aggregateId cannot be null");
        Objects.requireNonNull(fromAccountId, "fromAccountId cannot be null");
        Objects.requireNonNull(toAccountId, "toAccountId cannot be null");
        Objects.requireNonNull(amount, "amount cannot be null");
        Objects.requireNonNull(currency, "currency cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
    }

    @Override
    public String type() {
        return "transfer.initiated";
    }
}
