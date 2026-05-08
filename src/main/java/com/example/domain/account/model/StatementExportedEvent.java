package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record StatementExportedEvent(
    String aggregateId,
    String format,
    String artifactLocation,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "statement.exported";
    }
}