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
    public StatementExportedEvent {
        if (eventId == null) eventId = UUID.randomUUID().toString();
        if (occurredAt == null) occurredAt = Instant.now();
    }

    public StatementExportedEvent(String aggregateId, String format, String artifactLocation, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, format, artifactLocation, occurredAt);
    }

    @Override
    public String type() {
        return "statement.exported";
    }
}
