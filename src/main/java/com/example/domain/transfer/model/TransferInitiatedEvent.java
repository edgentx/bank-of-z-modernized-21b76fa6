package com.example.domain.transfer.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a transfer is successfully initiated.
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
    }

    public TransferInitiatedEvent(String aggregateId, String fromAccountId, String toAccountId, BigDecimal amount, String currency, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, fromAccountId, toAccountId, amount, currency, occurredAt);
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
