package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Event emitted when a statement is successfully exported.
 * S-9.
 */
public record StatementExportedEvent(
    String aggregateId,
    String accountId,
    String format,
    String artifactLocation,
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
