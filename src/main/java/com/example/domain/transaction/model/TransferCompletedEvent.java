package com.example.domain.transaction.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransferCompletedEvent(
        String aggregateId,
        String sourceAccountId,
        String destinationAccountId,
        BigDecimal amount,
        String currency,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "transfer.completed";
    }
}
