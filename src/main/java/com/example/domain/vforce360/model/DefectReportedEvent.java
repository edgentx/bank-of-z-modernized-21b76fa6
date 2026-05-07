package com.example.domain.vforce360.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Map;

/**
 * Event emitted when a defect is successfully reported.
 * Contains the link to the created GitHub issue.
 */
public record DefectReportedEvent(
    String defectId,
    String githubIssueUrl,
    Map<String, String> details,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "DefectReported";
    }

    @Override
    public String aggregateId() {
        return defectId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}