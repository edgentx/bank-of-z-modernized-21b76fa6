package com.example.domain.vforce360.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Map;

/**
 * Event emitted when a defect is reported.
 * Contains the GitHub URL and the Slack payload for validation.
 */
public record DefectReportedEvent(
    String defectId,
    String title,
    String githubUrl,
    String slackBody,
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
