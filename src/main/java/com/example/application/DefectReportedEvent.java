package com.example.application;

import java.time.Instant;

/**
 * Event representing the internal reporting of a defect (e.g., via Temporal).
 * Used to trigger the validation/notification workflow.
 */
public record DefectReportedEvent(
        String defectId,
        String title,
        String severity,
        String githubIssueUrl,
        Instant occurredAt
) {
    public DefectReportedEvent {
        if (defectId == null || defectId.isBlank()) {
            throw new IllegalArgumentException("defectId cannot be blank");
        }
        if (githubIssueUrl == null || githubIssueUrl.isBlank()) {
            throw new IllegalArgumentException("githubIssueUrl cannot be blank");
        }
    }
}