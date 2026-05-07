package com.example.domain.vforce360.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a defect is successfully reported.
 */
public record DefectReportedEvent(
        String event_Id,
        String defectId,
        String title,
        String slackNotificationBody,
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
