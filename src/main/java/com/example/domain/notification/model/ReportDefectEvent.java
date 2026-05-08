package com.example.domain.notification.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;

/**
 * Event emitted when a defect report is generated.
 * Contains the information to be formatted for Slack.
 */
public record ReportDefectEvent(
        String aggregateId,
        String defectId,
        String summary,
        String description,
        String severity,
        String githubIssueUrl,
        Instant occurredAt,
        Map<String, String> context
) implements DomainEvent {
    @Override
    public String type() {
        return "ReportDefectEvent";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}