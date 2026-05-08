package com.example.domain.validation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record ValidationResultRecordedEvent(
    String validationId,
    String component,
    boolean passed,
    String reason,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "ValidationResultRecorded";
    }

    @Override
    public String aggregateId() {
        return validationId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}