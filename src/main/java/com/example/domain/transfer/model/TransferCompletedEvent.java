package com.example.domain.transfer.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransferCompletedEvent(
        String eventId,
        String aggregateId,
        String sourceAccountId,
        String destinationAccountId,
        BigDecimal amount,
        Instant occurredAt
) implements DomainEvent {
    public TransferCompletedEvent(String aggregateId, String sourceAccountId, String destinationAccountId, BigDecimal amount, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, sourceAccountId, destinationAccountId, amount, occurredAt);
    }

    @Override
    public String type() {
        return "transfer.completed";
    }
}
