package com.example.domain.statement.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a Statement is successfully exported.
 * S-9: Implement ExportStatementCmd.
 */
public record StatementExportedEvent(
        String aggregateId,
        String format,
        String artifactLocation,
        Instant occurredAt,
        String eventId
) implements DomainEvent {
    public StatementExportedEvent {
        if (eventId == null || eventId.isBlank()) {
            eventId = UUID.randomUUID().toString();
        }
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

    // Factory method for clarity
    public static StatementExportedEvent create(String aggregateId, String format, String artifactLocation) {
        return new StatementExportedEvent(aggregateId, format, artifactLocation, Instant.now(), UUID.randomUUID().toString());
    }
}
