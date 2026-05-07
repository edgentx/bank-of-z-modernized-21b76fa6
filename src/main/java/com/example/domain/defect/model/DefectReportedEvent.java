package com.example.domain.defect.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a defect is reported, containing the GitHub issue URL.
 */
public record DefectReportedEvent(
    String aggregateId,
    String defectId,
    String gitHubIssueUrl,
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

    // Constructor for convenience if needed, though record handles it
    public static DefectReportedEvent create(String defectId, String url) {
        return new DefectReportedEvent(UUID.randomUUID().toString(), defectId, url, Instant.now());
    }
}
