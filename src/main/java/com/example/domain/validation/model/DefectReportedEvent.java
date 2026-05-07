package com.example.domain.validation.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record DefectReportedEvent(
    String aggregateId,
    String title,
    String githubUrl,
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

    /**
     * Formats the event body for Slack notification, ensuring the GitHub URL is included.
     * This addresses defect VW-454.
     */
    public String slackBody() {
        return String.format(
            "Defect Reported: %s\nGitHub Issue: %s",
            title,
            githubUrl
        );
    }
}