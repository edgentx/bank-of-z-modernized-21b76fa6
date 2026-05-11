package com.example.domain.reporting.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event published when a defect is successfully reported and recorded in external systems.
 */
public record DefectReportedEvent(
        String aggregateId,
        String defectId,
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
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
