package com.example.domain.vforce360.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a defect is reported to the PM diagnostic conversation.
 */
public record DefectReportedEvent(
        String aggregateId,
        String defectId,
        String githubUrl,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "DefectReportedEvent";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    public DefectReportedEvent(String defectId, String githubUrl) {
        this(UUID.randomUUID().toString(), defectId, githubUrl, Instant.now());
    }
}
