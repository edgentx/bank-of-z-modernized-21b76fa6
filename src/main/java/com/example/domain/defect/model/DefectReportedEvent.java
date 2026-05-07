package com.example.domain.defect.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a defect is reported.
 * Contains the generated GitHub issue URL.
 */
public record DefectReportedEvent(
    String type,
    String aggregateId,
    Instant occurredAt,
    String defectId,
    String githubIssueUrl
) implements DomainEvent {

    public DefectReportedEvent(String defectId, String githubIssueUrl, Instant occurredAt) {
        this(
            "DefectReportedEvent",
            UUID.randomUUID().toString(), // Aggregate ID for this event context
            occurredAt,
            defectId,
            githubIssueUrl
        );
    }

    @Override
    public String type() {
        return type;
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
