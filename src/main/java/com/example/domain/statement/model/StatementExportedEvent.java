package com.example.domain.statement.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

public class StatementExportedEvent implements DomainEvent {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatementExportedEvent that = (StatementExportedEvent) o;
        return Objects.equals(aggregateId, that.aggregateId) && Objects.equals(format, that.format) && Objects.equals(occurredAt, that.occurredAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateId, format, occurredAt);
    }
}
