package com.example.domain.vforce.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Event indicating a defect was successfully reported and linked to GitHub.
 */
public record DefectReportedEvent(
    String aggregateId,
    String defectId,
    String githubUrl,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "DefectReported";
    }
}
