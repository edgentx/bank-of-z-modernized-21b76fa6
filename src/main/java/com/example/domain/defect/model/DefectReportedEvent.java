package com.example.domain.defect.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a defect is successfully reported and logged in external systems.
 */
public record DefectReportedEvent(
        String aggregateId,
        String defectId,
        String githubIssueUrl,
        String slackChannel,
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

    // Static factory for cleaner creation
    public static DefectReportedEvent create(String defectId, String githubIssueUrl, String slackChannel) {
        return new DefectReportedEvent(
                UUID.randomUUID().toString(), // Aggregate ID
                defectId,
                githubIssueUrl,
                slackChannel,
                Instant.now()
        );
    }
}
