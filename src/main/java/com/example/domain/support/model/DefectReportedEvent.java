package com.example.domain.support.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;

/**
 * Domain event emitted when a defect is reported.
 * Contains the GitHub URL and the formatted Slack body.
 */
public record DefectReportedEvent(
        String defectId,
        String githubIssueUrl,
        String slackBody,
        Map<String, String> context,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "DefectReported";
    }

    @Override
    public String aggregateId() {
        return defectId();
    }

    @Override
    public Instant occurredAt() {
        return occurredAt();
    }
}
