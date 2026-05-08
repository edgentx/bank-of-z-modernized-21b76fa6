package com.example.domain.validation;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a defect is validated and reported.
 * Part of the shared domain context.
 */
public class ValidationReportedEvent implements DomainEvent {
    private final String id;
    private final Instant occurredAt;

    public ValidationReportedEvent(String id, Instant occurredAt) {
        this.id = id;
        this.occurredAt = occurredAt;
    }

    @Override
    public String type() {
        return "ValidationReported";
    }

    @Override
    public String aggregateId() {
        return id;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
