package com.example.domain.transfer.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * Event emitted when a transfer is initiated.
 * Part of Story S-13.
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
        // Default currency logic could be here, or enforced in Aggregate. Assuming USD for now if not passed.
    }

    @Override
    public String type() {
        return "transfer.initiated";
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
