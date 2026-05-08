package com.example.domain.statement.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a statement is successfully exported.
 */
public record StatementExportedEvent(
        String eventType,
        String aggregateId,
        Instant occurredAt,
        String format,
        String artifactLocation
) implements DomainEvent {

    public StatementExportedEvent(String aggregateId, String format, String artifactLocation) {
        this("statement.exported", aggregateId, Instant.now(), format, artifactLocation);
    }

    @Override
    public String type() {
        return eventType;
    }
}
