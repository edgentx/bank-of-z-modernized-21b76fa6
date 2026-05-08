package com.example.domain.defect.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Map;

/**
 * Event representing the successful reporting of a defect.
 * Should contain the GitHub URL link to the created issue.
 */
public record DefectReportedEvent(
    String defectId,
    String title,
    String githubUrl,
    Map<String, String> metadata,
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
