package com.example.domain.statement.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;

public record StatementExportedEvent(
    String aggregateId,
    String accountId,
    Instant startDate,
    Instant endDate,
    BigDecimal openingBalance,
    BigDecimal closingBalance,
    boolean isClosed,
    boolean hasBalanceMismatch
) implements DomainEvent {
    @Override
    public String type() {
        return "StatementExportedEvent";
    }

    @Override
    public Instant occurredAt() {
        return Instant.now();
    }
}
