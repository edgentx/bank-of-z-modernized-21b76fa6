package com.example.domain.validation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;

/**
 * Event representing that a defect has been reported.
 * Must contain the GitHub Issue URL if applicable.
 */
public record DefectReportedEvent(
        String defectId,
        String title,
        String githubIssueUrl, // Expected payload
        Map<String, String> metadata,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "DefectReported";
    }

    @Override
    public String aggregateId() {
        return defectId();
    }

    @Override
    public Instant occurredAt() {
        return occurredAt();
    }
}
