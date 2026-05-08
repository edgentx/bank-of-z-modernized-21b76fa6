package com.example.domain.statement.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record StatementExportedEvent(
        String aggregateId,
        String format,
        String artifactId,
        Instant occurredAt
) implements DomainEvent {
    public StatementExportedEvent(String aggregateId, String format, String artifactId, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.format = format;
        this.artifactId = artifactId;
        this.occurredAt = occurredAt;
    }

    @Override
    public String type() {
        return "statement.exported";
    }
}
