package com.example.domain.validation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record ValidationPassedEvent(
    String aggregateId,
    String targetUrl,
    boolean passed,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "ValidationPassed";
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
