package com.example.domain.defect.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a defect has been successfully reported to GitHub and Slack.
 */
public record DefectReportedEvent(
    String aggregateId,
    String githubUrl,
    String slackChannel,
    Instant occurredAt
) implements DomainEvent {

    public DefectReportedEvent(String aggregateId, String githubUrl, String slackChannel) {
        this(aggregateId, githubUrl, slackChannel, Instant.now());
    }

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
