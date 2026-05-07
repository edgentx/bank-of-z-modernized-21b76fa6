package com.example.domain.notification.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a defect is successfully formatted and validated.
 */
public record DefectReportedEvent(
    String notificationId,
    String title,
    String formattedBody,
    String githubIssueUrl,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "DefectReported";
    }

    @Override
    public String aggregateId() {
        return notificationId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
