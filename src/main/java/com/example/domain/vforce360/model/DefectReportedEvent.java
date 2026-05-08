package com.example.domain.vforce360.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;

/**
 * Event emitted when a defect is reported and the ticket URL is generated.
 */
public record DefectReportedEvent(
        String defectId,
        String title,
        String ticketUrl,
        String projectId,
        Instant occurredAt,
        Map<String, Object> metadata // Contains the Slack body details
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