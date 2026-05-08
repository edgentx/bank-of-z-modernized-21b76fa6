package com.example.domain.statement.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

public record StatementExportedEvent(
    String statementId,
    String format,
    String artifactLocation,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "statement.exported";
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