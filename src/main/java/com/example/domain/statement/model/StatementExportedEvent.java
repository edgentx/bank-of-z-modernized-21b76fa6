package com.example.domain.statement.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a statement is successfully exported.
 */
public record StatementExportedEvent(
        String aggregateId,
        String artifactId,
        String format,
        Instant occurredAt
) implements DomainEvent {

    public StatementExportedEvent(String aggregateId, String format) {
        this(aggregateId, UUID.randomUUID().toString(), format, Instant.now());
    }

    @Override
    public String type() {
        return "statement.exported";
    }
}
