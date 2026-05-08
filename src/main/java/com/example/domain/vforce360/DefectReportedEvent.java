package com.example.domain.vforce360;

import java.time.Instant;
import java.util.Map;

/**
 * Event published when a defect is reported via VForce360.
 * Contains the context required to generate the Slack message and GitHub issue.
 */
public record DefectReportedEvent(
        String defectId,
        String title,
        String description,
        String severity,
        String component,
        String projectId,
        String reportedBy,
        Instant occurredAt,
        Map<String, String> metadata
) {}
