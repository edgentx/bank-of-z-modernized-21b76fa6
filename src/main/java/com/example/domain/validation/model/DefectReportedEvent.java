package com.example.domain.validation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a defect is successfully reported and validated.
 * Expected to contain the GitHub URL and Slack notification details.
 */
public record DefectReportedEvent(
    String aggregateId,
    String defectId,
    String githubIssueUrl,
    String slackMessageBody,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "DefectReported";
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
