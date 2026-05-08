package com.example.domain.defect.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event published when a defect is reported.
 */
public record DefectReportedEvent(
    String defectId,
    String issueId,
    String githubUrl,
    String summary,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "DefectReportedEvent";
    }

    @Override
    public String aggregateId() {
        return defectId;
    }
}
