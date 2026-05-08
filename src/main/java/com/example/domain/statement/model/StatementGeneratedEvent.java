package com.example.domain.statement.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;

public record StatementGeneratedEvent(
    String aggregateId,
    String accountNumber,
    Instant periodEnd,
    BigDecimal openingBalance,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "statement.generated";
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
