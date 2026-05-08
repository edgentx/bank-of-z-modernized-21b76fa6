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
        if (eventId == null || eventId.isBlank()) {
            eventId = UUID.randomUUID().toString();
        }
    }

    @Override
    public String type() {
        return "statement.exported";
    }
}
