package com.example.domain.validation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a defect is successfully reported and logged.
 * Contains the resulting GitHub URL which should be propagated to Slack.
 */
public record DefectReportedEvent(
    String defectId,
    String description,
    String githubUrl,
    Instant occurredAt
) implements DomainEvent {
    @Override public String type() { return "DefectReported"; }
    @Override public String aggregateId() { return defectId; }
    @Override public Instant occurredAt() { return occurredAt; }
}
