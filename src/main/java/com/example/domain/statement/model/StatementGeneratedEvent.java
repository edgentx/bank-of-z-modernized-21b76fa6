package com.example.domain.statement.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a statement is successfully generated.
 */
public record StatementGeneratedEvent(
        String aggregateId,
        String accountNumber,
        Instant periodStart,
        Instant periodEnd,
        BigDecimal openingBalance,
        BigDecimal closingBalance,
        Instant occurredAt
) implements DomainEvent {
    public StatementGeneratedEvent(String aggregateId, String accountNumber, Instant periodStart, Instant periodEnd, BigDecimal openingBalance, BigDecimal closingBalance, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.accountNumber = accountNumber;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.openingBalance = openingBalance;
        this.closingBalance = closingBalance;
        this.occurredAt = occurredAt;
    }

    @Override
    public String type() {
        return "statement.generated";
    }
}
