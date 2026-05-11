package com.example.domain.statement.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Event emitted when a statement is successfully generated.
 * @param aggregateId The statement ID.
 * @param type The event type.
 * @param occurredAt When the event occurred.
 * @param accountNumber The associated account.
 * @param periodEnd The statement period end date.
 * @param openingBalance The opening balance.
 */
public record StatementGeneratedEvent(
    String aggregateId,
    String type,
    Instant occurredAt,
    String accountNumber,
    Instant periodEnd,
    BigDecimal openingBalance
) implements DomainEvent {
    public StatementGeneratedEvent(String aggregateId, String accountNumber, Instant periodEnd, BigDecimal openingBalance, Instant occurredAt) {
        this(aggregateId, "statement.generated", occurredAt, accountNumber, periodEnd, openingBalance);
    }
}
