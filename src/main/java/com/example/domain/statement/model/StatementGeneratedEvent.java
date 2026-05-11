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
        Instant periodEnd,
        BigDecimal openingBalance,
        Instant occurredAt
) implements DomainEvent {

    public StatementGeneratedEvent(String aggregateId, String accountNumber, Instant periodEnd, BigDecimal openingBalance, Instant occurredAt) {
        this.aggregateId = aggregateId != null ? aggregateId : UUID.randomUUID().toString();
        this.accountNumber = accountNumber;
        this.periodEnd = periodEnd;
        this.openingBalance = openingBalance;
        this.occurredAt = occurredAt;
    }

    @Override
    public String type() {
        return "statement.generated";
    }
}
