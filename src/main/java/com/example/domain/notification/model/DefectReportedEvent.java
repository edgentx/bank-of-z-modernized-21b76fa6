package com.example.domain.notification.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a defect report is generated.
 * Contains the payload intended for external delivery (e.g., Slack).
 */
public record DefectReportedEvent(
        String aggregateId,
        String messageBody,
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
