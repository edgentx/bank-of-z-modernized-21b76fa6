package com.example.domain.statement.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a Statement is successfully exported.
 */
public record StatementExportedEvent(
    String aggregateId,
    String format,
    String artifactLocation,
    Instant occurredAt
) implements DomainEvent {

    public StatementExportedEvent(String aggregateId, String format, String artifactLocation) {
        this(aggregateId, format, artifactLocation, Instant.now());
    }

    @Override
    public String type() {
        return "statement.exported";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
