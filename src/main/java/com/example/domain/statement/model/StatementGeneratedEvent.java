package com.example.domain.statement.model;

import com.example.domain.shared.DomainEvent;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a statement is successfully generated.
 */
public record StatementGeneratedEvent(
    String eventId,
    String statementId,
    String accountNumber,
    Instant periodStart,
    Instant periodEnd,
    BigDecimal openingBalance,
    BigDecimal closingBalance,
    Instant occurredAt
) implements DomainEvent {

    public StatementGeneratedEvent {
        if (eventId == null) eventId = UUID.randomUUID().toString();
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
}
