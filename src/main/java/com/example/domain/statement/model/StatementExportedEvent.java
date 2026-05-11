package com.example.domain.statement.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a Statement is successfully exported.
 * Used in Story S-9.
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

    // Java records can implement methods, but `aggregateId` field name matches interface requirement.
    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
