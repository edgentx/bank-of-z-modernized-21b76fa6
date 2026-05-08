package com.example.domain.validation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain Event representing a defect report filed in the system.
 * Emitted when a defect reporting workflow (e.g., Temporal) concludes.
 */
public class DefectReportedEvent implements DomainEvent {

    private final String eventId = UUID.randomUUID().toString();
    private final String aggregateId;
    private final String defectId;
    private final String description;
    private final String githubIssueUrl;
    private final Instant occurredAt;

    public DefectReportedEvent(String defectId, String description, String githubIssueUrl) {
        this.aggregateId = defectId; // Using defect ID as the aggregate identifier for this context
        this.defectId = defectId;
        this.description = description;
        this.githubIssueUrl = githubIssueUrl;
        this.occurredAt = Instant.now();
    }

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

    public String getDefectId() {
        return defectId;
    }

    public String getDescription() {
        return description;
    }

    public String getGithubIssueUrl() {
        return githubIssueUrl;
    }
}
