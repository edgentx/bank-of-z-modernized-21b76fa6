package com.example.domain.statement.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Domain event emitted when a Statement is successfully exported.
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

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}