package com.example.domain.transaction.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransferCompletedEvent(
    String eventId,
    String aggregateId,
    String fromAccountId,
    String toAccountId,
    BigDecimal amount,
    String currency,
    Instant occurredAt
) implements DomainEvent {
    public TransferCompletedEvent(String aggregateId, String fromAccountId, String toAccountId, BigDecimal amount, String currency, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, fromAccountId, toAccountId, amount, currency, occurredAt);
    }

    @Override
    public String type() {
        return "transfer.completed";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}