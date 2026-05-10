package com.example.domain.defect.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a defect is successfully reported and routed.
 * Contains the formatted Slack message body for verification.
 */
public record DefectReportedEvent(
    String defectId,
    String projectId,
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