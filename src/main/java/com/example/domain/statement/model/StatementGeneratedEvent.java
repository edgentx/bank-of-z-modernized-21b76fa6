package com.example.domain.statement.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class StatementGeneratedEvent implements DomainEvent {
    private final String eventId = UUID.randomUUID().toString();
    private final String statementId;
    private final String accountNumber;
    private final Instant periodEnd;
    private final BigDecimal openingBalance;
    private final BigDecimal closingBalance;
    private final Instant occurredAt;

    public StatementGeneratedEvent(String statementId, String accountNumber, Instant periodEnd, BigDecimal openingBalance, BigDecimal closingBalance, Instant occurredAt) {
        this.statementId = statementId;
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
        return statementId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    public String getStatementId() { return statementId; }
    public String getAccountNumber() { return accountNumber; }
    public Instant getPeriodEnd() { return periodEnd; }
    public BigDecimal getOpeningBalance() { return openingBalance; }
    public BigDecimal getClosingBalance() { return closingBalance; }
}
