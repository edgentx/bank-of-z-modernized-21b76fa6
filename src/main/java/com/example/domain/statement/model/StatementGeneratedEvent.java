package com.example.domain.statement.model;

import com.example.domain.shared.DomainEvent;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record StatementGeneratedEvent(
    String eventId,
    String aggregateId,
    String accountNumber,
    Instant periodEnd,
    BigDecimal openingBalance,
    BigDecimal closingBalance,
    Instant occurredAt
) implements DomainEvent {
    public StatementGeneratedEvent(String aggregateId, String accountNumber, Instant periodEnd, BigDecimal openingBalance, BigDecimal closingBalance, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, accountNumber, periodEnd, openingBalance, closingBalance, occurredAt);
    }

    @Override
    public String type() {
        return "statement.generated";
    }
}
