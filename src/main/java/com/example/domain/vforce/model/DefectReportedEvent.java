package com.example.domain.vforce.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a defect is reported.
 * This file is a STUB implementation designed to FAIL the tests above (Red Phase).
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
     * This stub returns null, causing the validation test to fail.
     */
    public String getGitHubUrl() {
        return null; // Red Phase: Intentional failure
    }
}
