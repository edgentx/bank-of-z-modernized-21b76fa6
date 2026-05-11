package com.example.domain.statement.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;

public record StatementGeneratedEvent(
        String aggregateId,
        String accountNumber,
        Instant periodStart,
        Instant periodEnd,
        BigDecimal openingBalance,
        BigDecimal closingBalance,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "statement.generated";
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}
