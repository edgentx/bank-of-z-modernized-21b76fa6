package com.example.domain.statement.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record StatementGeneratedEvent(
        String aggregateId,
        String accountId,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal openingBalance,
        BigDecimal closingBalance,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "statement.generated";
    }
}
