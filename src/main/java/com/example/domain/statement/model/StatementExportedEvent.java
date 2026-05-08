package com.example.domain.statement.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record StatementExportedEvent(
        String aggregateId,
        String format,
        Instant occurredAt
) implements DomainEvent {
    public StatementExportedEvent(String id, String format, Instant occurredAt) {
        this.aggregateId = id;
        this.format = format;
        this.occurredAt = occurredAt;
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
}
