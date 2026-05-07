package com.example.domain.validation.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Event published when a defect is reported.
 * MUST contain the GitHub URL for the Slack body validation.
 */
public record DefectReportedEvent(
    String defectId,
    String title,
    String severity,
    String githubIssueUrl,
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
