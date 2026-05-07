package com.example.domain.defect.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a defect is successfully reported.
 */
public record DefectReportedEvent(
    String eventId,
    String defectId,
    String summary,
    String severity,
    String gitHubIssueUrl,
    Instant occurredAt
) implements DomainEvent {

    public DefectReportedEvent(String defectId, String summary, String severity, String gitHubIssueUrl, Instant occurredAt) {
        this(UUID.randomUUID().toString(), defectId, summary, severity, gitHubIssueUrl, occurredAt);
    }

    @Override
    public String type() {
        return "DefectReported";
    }

    @Override
    public String aggregateId() {
        return defectId();
    }

    @Override
    public Instant occurredAt() {
        return occurredAt();
    }
}
