package com.example.domain.statement.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

public record StatementExportedEvent(
        String aggregateId,
        String format,
        String artifactLocation,
        Instant occurredAt
) implements DomainEvent {
    public StatementExportedEvent {
        Objects.requireNonNull(aggregateId, "aggregateId cannot be null");
        Objects.requireNonNull(format, "format cannot be null");
        Objects.requireNonNull(artifactLocation, "artifactLocation cannot be null");
    }

    @Override
    public String type() {
        return "statement.exported";
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
