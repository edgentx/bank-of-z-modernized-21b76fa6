package com.example.domain.defect.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a defect is reported.
 * Contains the formatted payload for the Slack notification.
 */
public record DefectReportedEvent(
    String defectId,
    String slackBody,
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
