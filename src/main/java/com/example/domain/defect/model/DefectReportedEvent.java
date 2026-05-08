package com.example.domain.defect.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a defect is successfully reported.
 * MUST contain the GitHub URL.
 */
public record DefectReportedEvent(
    String aggregateId,
    String title,
    String githubUrl,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "DefectReportedEvent";
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
