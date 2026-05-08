package com.example.domain.validation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a defect is successfully reported.
 * Contains the GitHub URL required for the Slack notification body.
 */
public record DefectReportedEvent(
    String eventId,
    String aggregateId,
    String defectId,
    String summary,
    String githubUrl,
    Instant occurredAt
) implements DomainEvent {

    public DefectReportedEvent(String aggregateId, String defectId, String summary, String githubUrl, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, defectId, summary, githubUrl, occurredAt);
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
