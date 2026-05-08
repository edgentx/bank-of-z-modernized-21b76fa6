package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a statement is successfully generated.
 * S-8: Implement GenerateStatementCmd on Statement.
 */
public class StatementGeneratedEvent implements DomainEvent {
    private final String aggregateId;
    private final String accountNumber;
    private final Instant periodEnd;
    private final BigDecimal openingBalance;
    private final BigDecimal closingBalance;
    private final Instant occurredAt;

    public StatementGeneratedEvent(
            String aggregateId,
            String accountNumber,
            Instant periodEnd,
            BigDecimal openingBalance,
            BigDecimal closingBalance,
            Instant occurredAt
    ) {
        this.aggregateId = aggregateId;
        this.accountNumber = accountNumber;
        this.periodEnd = periodEnd;
        this.openingBalance = openingBalance;
        this.closingBalance = closingBalance;
        this.occurredAt = occurredAt;
    }

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

    public String accountNumber() { return accountNumber; }
    public Instant periodEnd() { return periodEnd; }
    public BigDecimal openingBalance() { return openingBalance; }
    public BigDecimal closingBalance() { return closingBalance; }
}
