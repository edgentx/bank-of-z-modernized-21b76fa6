package com.example.domain.transaction.model;

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
        if (eventId == null) eventId = UUID.randomUUID().toString();
        if (occurredAt == null) occurredAt = Instant.now();
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
