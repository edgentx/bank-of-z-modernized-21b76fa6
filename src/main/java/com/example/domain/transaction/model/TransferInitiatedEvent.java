package com.example.domain.transaction.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a transfer has been successfully initiated.
 * S-13: Implement InitiateTransferCmd on Transfer (transaction-processing).
 */
public record TransferInitiatedEvent(
        String eventId,
        String aggregateId,
        String fromAccountId,
        String toAccountId,
        BigDecimal amount,
        String currency,
        Instant occurredAt
) implements DomainEvent {

    public TransferInitiatedEvent {
        Objects.requireNonNull(eventId, "eventId required");
        Objects.requireNonNull(aggregateId, "aggregateId required");
        Objects.requireNonNull(fromAccountId, "fromAccountId required");
        Objects.requireNonNull(toAccountId, "toAccountId required");
        Objects.requireNonNull(amount, "amount required");
        Objects.requireNonNull(currency, "currency required");
        Objects.requireNonNull(occurredAt, "occurredAt required");
    }

    public TransferInitiatedEvent(String aggregateId, String fromAccountId, String toAccountId, BigDecimal amount, String currency, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, fromAccountId, toAccountId, amount, currency, occurredAt);
    }

    @Override
    public String type() {
        return "transfer.initiated";
    }
}
