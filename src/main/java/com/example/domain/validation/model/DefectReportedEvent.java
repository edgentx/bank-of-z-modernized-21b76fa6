package com.example.domain.validation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain Event: Defect Reported
 * Published when a defect validation fails in VForce360.
 * This event encapsulates the state required to notify external systems
 * (e.g., Slack) and provide context (links) for resolution.
 */
public class DefectReportedEvent implements DomainEvent {

    private final String eventId;
    private final String defectId;
    private final String projectId;
    private final String description;
    private final Instant occurredAt;

    public DefectReportedEvent(String defectId, String projectId, String description, Instant occurredAt) {
        this.eventId = UUID.randomUUID().toString();
        this.defectId = defectId;
        this.projectId = projectId;
        this.description = description;
        this.occurredAt = occurredAt;
    }

    @Override
    public String type() {
        return "DefectReported";
    }

    @Override
    public String aggregateId() {
        return projectId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    public String getEventId() {
        return eventId;
    }

    public String getDefectId() {
        return defectId;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Constructs the Slack message body containing the GitHub issue link.
     * 
     * Acceptance Criteria:
     * - The validation no longer exhibits the reported behavior (missing URL)
     * - Slack body includes GitHub issue: <url>
     * 
     * Slack Link Format: <URL|Text>
     * 
     * @return Formatted string for Slack notification.
     */
    public String getSlackBody() {
        // Assumption: The GitHub repo URL structure is standard.
        // Defect ID 'VW-454' maps to issue '454'.
        String issueNumber = defectId.replace("VW-", "").replace("S-FB-", ""); 
        
        String baseUrl = "https://github.com/bank-of-z/vforce360/issues/";
        String fullUrl = baseUrl + issueNumber;
        
        // Formatting with Slack markup <url|text>
        return String.format(
            "Defect Reported: %s\nGitHub issue: <%s|%s>",
            description,
            fullUrl,
            defectId
        );
    }
}
