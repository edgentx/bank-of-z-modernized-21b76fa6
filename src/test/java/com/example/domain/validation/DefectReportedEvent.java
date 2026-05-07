package com.example.domain.validation;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Map;

/**
 * Event emitted when a defect is successfully reported and logged.
 * Contains the formatted Slack body and the generated GitHub issue URL.
 */
public record DefectReportedEvent(
    String defectId,
    String title,
    String severity,
    String component,
    String projectId,
    String slackBody,
    String githubUrl,
    Map<String, String> context,
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
