package com.example.domain.statement.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;

// Internal event for setup/simulation of the aggregate state
public record StatementGeneratedEvent(
        String statementId,
        String accountId,
        BigDecimal openingBalance,
        BigDecimal closingBalance,
        boolean isPeriodClosed,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() { return "statement.generated"; }
    @Override
    public String aggregateId() { return statementId; }
    @Override
    public Instant occurredAt() { return occurredAt; }
}