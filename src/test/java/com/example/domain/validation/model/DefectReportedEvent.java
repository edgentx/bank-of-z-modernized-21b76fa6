package com.example.domain.validation.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a defect is successfully reported.
 * Must contain the GitHub URL for the Slack body validation.
 */
public record DefectReportedEvent(
    String defectId,
    String title,
    String githubIssueUrl, // The critical field for S-FB-1
    String severity,
    String component,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "DefectReported";
    }

    @Override
    public String aggregateId() {
        return defectId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}