package com.example.domain.vforce.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

/**
 * Event emitted when a defect is reported.
 * Contains the GitHub URL as required by defect VW-454.
 */
public class DefectReportedEvent implements DomainEvent {

    private final String aggregateId;
    private final String title;
    private final String description;
    private final String severity;
    private final String type;
    private final Instant occurredAt;

    public DefectReportedEvent(String aggregateId, String title, String description, String severity, String type, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.title = title;
        this.description = description;
        this.severity = severity;
        this.type = type;
        this.occurredAt = occurredAt;
    }

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

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getSeverity() { return severity; }
    public String getType() { return type; }

    /**
     * CRITICAL METHOD FOR VW-454.
     * Returns the fully constructed GitHub URL for inclusion in Slack alerts.
     */
    public String getGitHubUrl() {
        return "https://github.com/bank-of-z/issues/" + this.aggregateId;
    }
}