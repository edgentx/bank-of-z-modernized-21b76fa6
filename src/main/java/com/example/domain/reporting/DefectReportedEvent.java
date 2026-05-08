package com.example.domain.reporting.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Map;

/**
 * Event emitted when a defect is detected by the VForce360 system.
 * Contains the raw payload to be formatted for external endpoints (e.g., Slack).
 */
public record DefectReportedEvent(
    String aggregateId,
    String defectId,
    String description,
    Map<String, String> metadata,
    Instant occurredAt
) implements DomainEvent {
    @Override public String type() { return "DefectReported"; }
    @Override public String aggregateId() { return aggregateId; }
    @Override public Instant occurredAt() { return occurredAt; }
}
