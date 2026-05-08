package com.example.domain.statement.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Event emitted when a statement is successfully generated.
 * S-8
 */
public record StatementGeneratedEvent(
    String aggregateId,
    String accountNumber,
    LocalDate periodStart,
    LocalDate periodEnd,
    BigDecimal openingBalance,
    BigDecimal closingBalance,
    Instant occurredAt
) implements DomainEvent {

    public StatementGeneratedEvent {
        // Defensive copy/validation if necessary
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
}
