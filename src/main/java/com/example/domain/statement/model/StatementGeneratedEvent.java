package com.example.domain.statement.model;

import com.example.domain.shared.DomainEvent;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record StatementGeneratedEvent(
    String statementId, String accountId, LocalDate periodStart, LocalDate periodEnd,
    BigDecimal openingBalance, BigDecimal closingBalance, Instant occurredAt
) implements DomainEvent {
  @Override public String type() { return "statement.generated"; }
  @Override public String aggregateId() { return statementId; }
}
