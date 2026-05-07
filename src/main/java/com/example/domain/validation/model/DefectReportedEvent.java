package com.example.domain.validation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Domain event emitted when a defect is successfully validated and reported.
 */
public record DefectReportedEvent(
    String validationId,
    String title,
    String githubIssueUrl,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "DefectReported";
    }

    @Override
    public String aggregateId() {
        return validationId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
