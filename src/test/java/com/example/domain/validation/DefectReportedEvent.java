package com.example.domain.validation;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain event representing a defect that has been identified and needs to be reported.
 * This is the trigger for the Slack notification workflow.
 */
public record DefectReportedEvent(
        String aggregateId,
        String defectId,
        String title,
        String description,
        String githubIssueUrl,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "DefectReported";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}