package com.example.domain.diagnostic;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Domain event published when a defect is successfully reported.
 * Contains the formatted Slack body ready for dispatch by the temporal worker.
 */
public record DefectReportedEvent(
    String issueId,
    String severity,
    String component,
    String description,
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
        return issueId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
