package com.example.domain.defect.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a defect is successfully reported to Slack/GitHub.
 */
public record DefectReportedEvent(
        String aggregateId,
        String defectId,
        String githubUrl,
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
}
