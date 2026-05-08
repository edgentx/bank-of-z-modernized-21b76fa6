package com.example.domain.defect.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Map;

/**
 * Event emitted when a defect is successfully reported.
 * Contains the generated GitHub URL which must be propagated to Slack.
 */
public record DefectReportedEvent(
    String type,
    String aggregateId,
    Instant occurredAt,
    String defectId,
    String githubUrl,
    Map<String, Object> payload
) implements DomainEvent {

    public DefectReportedEvent(String defectId, String githubUrl, Map<String, Object> payload, Instant occurredAt) {
        this("DefectReported", defectId, occurredAt, defectId, githubUrl, payload);
    }
}
