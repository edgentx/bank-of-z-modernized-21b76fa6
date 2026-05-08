package com.example.domain.validation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Event emitted when a defect is successfully reported.
 */
public record DefectReportedEvent(
        String aggregateId,
        String defectId,
        String slackChannel,
        String messageBody,
        Map<String, String> metadata,
        Instant occurredAt
) implements DomainEvent {

    public DefectReportedEvent(String aggregateId, String defectId, String slackChannel, String messageBody, Map<String, String> metadata) {
        this(aggregateId, defectId, slackChannel, messageBody, metadata, Instant.now());
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
