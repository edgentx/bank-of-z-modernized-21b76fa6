package com.example.domain.defect.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event representing a defect that has been reported and requires validation.
 */
public record DefectReportedEvent(
        String defectId,
        String title,
        String githubUrl,
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
