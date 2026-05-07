package com.example.domain.vforce;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain event representing a defect reported via the VForce360 PM diagnostic conversation.
 * This corresponds to the input trigger described in the story.
 */
public record DefectReportedEvent(
        String aggregateId,
        String defectId,
        String title,
        String description,
        String severity,
        String projectId,
        Instant occurredAt
) implements DomainEvent {
    public DefectReportedEvent {
        if (aggregateId == null) aggregateId = UUID.randomUUID().toString();
        if (occurredAt == null) occurredAt = Instant.now();
    }

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
