package com.example.domain.transfer.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransferInitiatedEvent(
        String aggregateId,
        String fromAccountId,
        String toAccountId,
        BigDecimal amount,
        String currency,
        Instant occurredAt
) implements DomainEvent {
    public TransferInitiatedEvent {
        // Ensure immutable defaults
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
