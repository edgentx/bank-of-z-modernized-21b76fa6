package com.example.domain.defect.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

/**
 * Event published when a defect is successfully reported and validated.
 */
public record DefectReportedEvent(
    String defectId,
    String title,
    String severity,
    String issueUrl, // The GitHub URL generated
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
