package com.example.domain.defect.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

/**
 * Event published when a defect is successfully reported.
 * Contains the GitHub URL which must be present in the Slack notification.
 */
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
}
