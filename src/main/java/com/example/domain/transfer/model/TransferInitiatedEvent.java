package com.example.domain.transfer.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * Domain event emitted when a transfer is initiated.
 * S-13 Implementation.
 */
public record TransferInitiatedEvent(
    String aggregateId,
    String fromAccount,
    String toAccount,
    BigDecimal amount,
    Instant occurredAt
) implements DomainEvent {

    public TransferInitiatedEvent {
        Objects.requireNonNull(aggregateId, "aggregateId required");
        Objects.requireNonNull(fromAccount, "fromAccount required");
        Objects.requireNonNull(toAccount, "toAccount required");
        Objects.requireNonNull(amount, "amount required");
        Objects.requireNonNull(occurredAt, "occurredAt required");
    }

    @Override
    public String type() {
        return "transfer.initiated";
    }
}
