package com.example.domain.transfer;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

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
        Objects.requireNonNull(eventId);
        Objects.requireNonNull(aggregateId);
    }

    public TransferInitiatedEvent(String aggregateId, String fromAccountId, String toAccountId, BigDecimal amount, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, fromAccountId, toAccountId, amount, "USD", occurredAt);
    }

    @Override
    public String type() {
        return "transfer.initiated";
    }
}
