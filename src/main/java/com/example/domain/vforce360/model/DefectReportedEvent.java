package com.example.domain.vforce360.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.List;

/**
 * Event emitted when a defect is successfully reported and tracked.
 */
public record DefectReportedEvent(
    String type,
    String aggregateId,
    Instant occurredAt,
    String defectId,
    String title,
    String severity,
    String component,
    String projectId,
    String githubUrl
) implements DomainEvent {
    public DefectReportedEvent {
        if (type == null) type = "DefectReported";
    }
}