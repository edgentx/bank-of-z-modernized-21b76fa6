package com.example.domain.validation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Event representing the successful reporting of a defect.
 */
public record DefectReportedEvent(
        String eventId,
        String defectId,
        String title,
        String severity,
        String component,
        Map<String, Object> context,
        Instant occurredAt
) implements DomainEvent {

    public DefectReportedEvent(String defectId, String title, String severity, String component, Map<String, Object> context, Instant occurredAt) {
        this(UUID.randomUUID().toString(), defectId, title, severity, component, context, occurredAt);
    }

    @Override
    public String type() {
        return "DefectReported";
    }

    @Override
    public String aggregateId() {
        return defectId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
