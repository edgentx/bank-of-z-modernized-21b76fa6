package com.example.defect.domain;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Map;

/**
 * Event emitted when a defect is successfully reported to GitHub.
 * Contains the GitHub URL that must be verified in Slack.
 */
public record DefectReportedEvent(
    String defectId,
    String githubUrl,
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
