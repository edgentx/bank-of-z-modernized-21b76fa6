package com.example.domain.defect.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Domain event published when a defect is reported.
 */
public record DefectReportedEvent(
        String eventId,
        String defectId,
        String title,
        String githubUrl,
        Map<String, Object> metadata,
        Instant occurredAt
) implements DomainEvent {

    public DefectReportedEvent(String defectId, String title, String githubUrl, Map<String, Object> metadata, Instant occurredAt) {
        this(UUID.randomUUID().toString(), defectId, title, githubUrl, metadata, occurredAt);
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
