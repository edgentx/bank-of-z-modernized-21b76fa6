package com.example.domain.statement.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class StatementExportedEvent implements DomainEvent {
    private final String eventId = UUID.randomUUID().toString();
    private final String aggregateId;
    private final String format;
    private final Instant occurredAt;

    public StatementExportedEvent(String aggregateId, String format, Instant occurredAt) {
        this.aggregateId = aggregateId;
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

    public String format() {
        return format;
    }
}