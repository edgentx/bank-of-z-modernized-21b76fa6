package com.example.domain.statement.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a statement is successfully exported.
 */
public record StatementExportedEvent(
        String aggregateId,
        String format,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "statement.exported";
    }
}
