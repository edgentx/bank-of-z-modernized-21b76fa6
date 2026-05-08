package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record StatementGeneratedEvent(
    String aggregateId,
    String accountNumber,
    LocalDate periodEnd,
    BigDecimal openingBalance,
    BigDecimal closingBalance, // Likely calculated or fetched, 0 for now if not provided in cmd
    Instant occurredAt
) implements DomainEvent {
    public StatementGeneratedEvent(String aggregateId, String accountNumber, LocalDate periodEnd, BigDecimal openingBalance, BigDecimal closingBalance) {
        this(aggregateId, accountNumber, periodEnd, openingBalance, closingBalance, Instant.now());
    }

    @Override
    public String type() {
        return "statement.generated";
    }
}
