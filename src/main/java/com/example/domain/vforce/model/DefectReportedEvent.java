package com.example.domain.vforce.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;

/**
 * Event emitted when a defect is reported.
 * Contains the expected Slack body format.
 */
public record DefectReportedEvent(
    String aggregateId,
    String slackBody,
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
}
