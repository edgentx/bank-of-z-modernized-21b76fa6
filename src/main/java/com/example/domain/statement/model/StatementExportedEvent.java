package com.example.domain.statement.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a statement is successfully exported.
 * Part of Story S-9: ExportStatementCmd.
 */
public record StatementExportedEvent(
    String eventId,
    String aggregateId,
    String format,
    String storageLocation,
    Instant occurredAt
) implements DomainEvent {
    public StatementExportedEvent(String aggregateId, String format, String storageLocation) {
        this(UUID.randomUUID().toString(), aggregateId, format, storageLocation, Instant.now());
    }

    @Override
    public String type() {
        return "statement.exported";
    }
}
