package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a statement is successfully exported.
 */
public record StatementExportedEvent(
        String type,
        String aggregateId,
        String format,
        Instant occurredAt
) implements DomainEvent {
    public StatementExportedEvent {
        if (type == null) type = "statement.exported";
    }

    public static StatementExportedEvent create(String statementId, String format) {
        return new StatementExportedEvent("statement.exported", statementId, format, Instant.now());
    }
}
