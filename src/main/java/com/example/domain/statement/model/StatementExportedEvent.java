package com.example.domain.statement.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record StatementExportedEvent(
        String eventId,
        String aggregateId,
        String format,
        String artifactLocation,
        Instant occurredAt
) implements DomainEvent {
    public StatementExportedEvent(String aggregateId, String format, String artifactLocation, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, format, artifactLocation, occurredAt);
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
