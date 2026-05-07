package com.example.domain.vforce360.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a defect is successfully validated and reported.
 * Contains the generated GitHub Issue URL.
 */
public record DefectReportedEvent(
    String aggregateId,
    String defectId,
    String title,
    String severity,
    String description,
    String githubIssueUrl, // The critical field for VW-454
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "DefectReported";
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
