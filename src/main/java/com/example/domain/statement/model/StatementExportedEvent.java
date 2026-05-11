package com.example.domain.statement.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;

public record StatementExportedEvent(
    String aggregateId,
    String format,
    BigDecimal closingBalance,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "statement.exported";
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}
