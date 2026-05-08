package com.example.domain.defect.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a defect is successfully reported and validated.
 */
public record DefectReportedEvent(
    String defectId,
    String title,
    String githubIssueUrl,
    Instant occurredAt
) implements DomainEvent {

    public DefectReportedEvent(String defectId, String title, String githubIssueUrl, Instant occurredAt) {
        this.defectId = defectId;
        this.title = title;
        this.githubIssueUrl = githubIssueUrl;
        this.occurredAt = occurredAt;
    }

    @Override
    public String type() {
        return "DefectReported";
    }

    @Override
    public String aggregateId() {
        return defectId;
    }
}
