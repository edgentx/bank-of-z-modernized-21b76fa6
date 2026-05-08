package com.example.domain.defect.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

/**
 * Event emitted when a defect is reported.
 * Contains the GitHub URL generated during the process.
 */
public record DefectReportedEvent(
        String aggregateId,
        String projectId,
        String title,
        String githubIssueUrl,
        Instant occurredAt
) implements DomainEvent {
    @Override public String type() { return "DefectReported"; }
    @Override public String aggregateId() { return aggregateId; }
    @Override public Instant occurredAt() { return occurredAt; }
}
