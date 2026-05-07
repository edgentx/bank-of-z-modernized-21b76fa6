package com.example.domain.defect.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Map;

/**
 * Event emitted when a defect is reported.
 * Must contain the GitHub URL in the slackBody.
 */
public record DefectReportedEvent(
        String defectId,
        String title,
        String projectId,
        String slackBody,
        String githubUrl,
        Map<String, String> metadata,
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