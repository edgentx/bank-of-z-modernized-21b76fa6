package com.example.domain.validation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Domain event published when a defect is reported.
 * Contains the GitHub Issue URL required for Slack notifications.
 */
public record DefectReportedEvent(
    String defectId,
    String title,
    String description,
    String githubIssueUrl,
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
        return occurredAt;
    }
}