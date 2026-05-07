package com.example.domain.validation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;

/**
 * Event emitted when a defect is reported.
 * Should contain the formatted Slack body and the GitHub issue URL.
 */
public record DefectReportedEvent(
        String defectId,
        String slackBody,
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