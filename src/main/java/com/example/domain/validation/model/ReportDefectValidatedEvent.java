package com.example.domain.validation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a defect report passes initial validation.
 */
public record ReportDefectValidatedEvent(
    String eventId,
    String aggregateId,
    String storyId,
    String title,
    String severity,
    String component,
    Instant occurredAt
) implements DomainEvent {

    public ReportDefectValidatedEvent(String aggregateId, String storyId, String title, String severity, String component, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, storyId, title, severity, component, occurredAt);
    }

    @Override
    public String type() {
        return "ReportDefectValidated";
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
