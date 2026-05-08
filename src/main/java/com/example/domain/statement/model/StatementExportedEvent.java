package com.example.domain.statement.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record StatementExportedEvent(
        String eventId,
        String statementId,
        String format,
        Instant occurredAt
) implements DomainEvent {
    public StatementExportedEvent(String statementId, String format, Instant occurredAt) {
        this(UUID.randomUUID().toString(), statementId, format, occurredAt);
    }

    @Override
    public String type() {
        return "statement.exported";
    }

    @Override
    public String aggregateId() {
        return statementId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
