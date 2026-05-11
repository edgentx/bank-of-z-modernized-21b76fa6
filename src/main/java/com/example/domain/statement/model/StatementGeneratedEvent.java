package com.example.domain.statement.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class StatementGeneratedEvent implements DomainEvent {
    private final String statementId;
    private final String accountNumber;
    private final LocalDate periodStart;
    private final LocalDate periodEnd;
    private final BigDecimal openingBalance;
    private final BigDecimal closingBalance;
    private final Instant occurredAt;

    public StatementGeneratedEvent(String statementId,
                                   String accountNumber,
                                   LocalDate periodStart,
                                   LocalDate periodEnd,
                                   BigDecimal openingBalance,
                                   BigDecimal closingBalance,
                                   Instant occurredAt) {
        this.statementId = statementId;
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

    @Override
    public String aggregateId() {
        return statementId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    public String statementId() { return statementId; }
    public String accountNumber() { return accountNumber; }
    public LocalDate periodStart() { return periodStart; }
    public LocalDate periodEnd() { return periodEnd; }
    public BigDecimal openingBalance() { return openingBalance; }
    public BigDecimal closingBalance() { return closingBalance; }
}