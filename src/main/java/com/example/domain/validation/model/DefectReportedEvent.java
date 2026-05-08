package com.example.domain.validation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a defect is successfully reported in the system.
 * MUST contain the GitHub URL for the VW-454 requirement.
 */
public record DefectReportedEvent(
    String aggregateId,
    String code,
    String summary,
    String severity,
    String gitHubIssueUrl, // Critical field for VW-454
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
