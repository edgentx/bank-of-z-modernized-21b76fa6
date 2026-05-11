package com.example.domain.statement.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a statement is successfully exported.
 * S-9
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

    // Constructor or factory if needed, though record works fine.
    // We ensure contract compliance via the explicit override above.
}
