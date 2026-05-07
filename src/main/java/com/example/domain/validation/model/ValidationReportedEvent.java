package com.example.domain.validation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record ValidationReportedEvent(
    String validationId,
    String summary,
    String severity,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "ValidationReported";
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