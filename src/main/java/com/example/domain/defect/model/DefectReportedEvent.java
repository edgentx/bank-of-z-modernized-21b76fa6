package com.example.domain.defect.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

/**
 * Event published when a defect is reported to VForce360.
 * Contains the metadata required to generate the notification.
 */
public record DefectReportedEvent(
        String aggregateId,
        String title,
        String description,
        String githubUrl,
        String channel,
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
