package com.example.domain.report_defect.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a defect is successfully posted to GitHub and Slack.
 */
public record ReportDefectPostedEvent(
    String aggregateId,
    String eventType,
    String githubIssueUrl,
    String defectId,
    String description,
    Severity severity,
    Instant occurredAt
) implements DomainEvent {
    public ReportDefectPostedEvent {
        if (eventType == null) eventType = "ReportDefectPostedEvent";
    }

    public static ReportDefectPostedEvent create(String defectId, String description, Severity severity, String githubUrl) {
        return new ReportDefectPostedEvent(
            UUID.randomUUID().toString(), // Event Aggregate ID (Correlation ID)
            "ReportDefectPostedEvent",
            githubUrl,
            defectId,
            description,
            severity,
            Instant.now()
        );
    }

    @Override public String type() { return eventType; }
    @Override public String aggregateId() { return aggregateId; }
    @Override public Instant occurredAt() { return occurredAt; }
}
