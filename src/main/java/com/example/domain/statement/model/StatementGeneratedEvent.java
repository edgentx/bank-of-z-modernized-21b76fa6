package com.example.domain.statement.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Event emitted when a statement is successfully generated.
 */
public record StatementGeneratedEvent(
    String aggregateId,
    String accountNumber,
    LocalDate periodEnd,
    BigDecimal openingBalance,
    BigDecimal closingBalance,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "statement.generated";
    }
}
