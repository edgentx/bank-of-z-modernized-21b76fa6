package com.example.domain.statement.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record StatementExportedEvent(
        String aggregateId,
        String format,
        String artifactLocation,
        Instant occurredAt
) implements DomainEvent {
    public StatementExportedEvent(String id, String format, Instant occurredAt) {
        this(id, format, "s3-bucket/" + UUID.randomUUID() + ".pdf", occurredAt);
    }

    @Override
    public String type() {
        return "statement.exported";
    }
}
