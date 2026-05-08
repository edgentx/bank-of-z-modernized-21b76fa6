package com.example.domain.validation;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event published when a defect is reported and validated.
 * Contains the GitHub URL generated for the defect.
 */
public record DefectReportedEvent(
        String aggregateId,
        String githubIssueUrl,
        Instant occurredAt
) implements DomainEvent {

    public DefectReportedEvent {
        if (aggregateId == null || aggregateId.isBlank()) {
            throw new IllegalArgumentException("aggregateId cannot be null or blank");
        }
        if (githubIssueUrl == null || githubIssueUrl.isBlank()) {
            throw new IllegalArgumentException("githubIssueUrl cannot be null or blank");
        }
        if (occurredAt == null) {
            throw new IllegalArgumentException("occurredAt cannot be null");
        }
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