package com.example.domain.defect;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Optional;

/**
 * Domain Event representing that a defect has been reported and validated.
 * This is the Event we expect to be produced by the Validation Workflow.
 */
public record DefectReportedEvent(
        String type,
        String aggregateId,
        Instant occurredAt,
        String defectId,
        String githubUrl
) implements DomainEvent {
    public DefectReportedEvent(String aggregateId, String defectId, String githubUrl) {
        this("DefectReported", aggregateId, Instant.now(), defectId, githubUrl);
    }

    /**
     * Helper to extract the URL safely.
     */
    public Optional<String> getGithubUrl() {
        return Optional.ofNullable(githubUrl);
    }
}
