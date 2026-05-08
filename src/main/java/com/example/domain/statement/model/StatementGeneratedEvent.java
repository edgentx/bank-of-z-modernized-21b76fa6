package com.example.domain.statement.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record StatementGeneratedEvent(
    String aggregateId,
    String accountNumber,
    LocalDate periodEnd,
    Instant occurredAt,
    BigDecimal openingBalance,
    BigDecimal closingBalance
) implements DomainEvent {
    @Override public String type() { return "statement.generated"; }
    @Override public String aggregateId() { return aggregateId; }
    @Override public Instant occurredAt() { return occurredAt; }
}