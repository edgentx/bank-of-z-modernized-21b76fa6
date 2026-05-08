package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a statement is successfully generated.
 */
public record StatementGeneratedEvent(
    String eventId,
    String aggregateId,
    String accountNumber,
    Instant periodStart,
    Instant periodEnd,
    BigDecimal openingBalance,
    BigDecimal closingBalance,
    Instant occurredAt
) implements DomainEvent {

    public StatementGeneratedEvent(
        String aggregateId,
        String accountNumber,
        Instant periodStart,
        Instant periodEnd,
        BigDecimal openingBalance,
        BigDecimal closingBalance,
        Instant occurredAt
    ) {
        this(
            UUID.randomUUID().toString(),
            aggregateId,
            accountNumber,
            periodStart,
            periodEnd,
            openingBalance,
            closingBalance,
            occurredAt
        );
    }

    @Override
    public String type() {
        return "statement.generated";
    }
}
