package com.example.domain.vforce360.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event published when a defect is successfully reported.
 */
public record DefectReportedEvent(
    String aggregateId,
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
