package com.example.domain.vforce360.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Event published when a defect report is successfully sent to VForce360.
 */
public record DefectReportedEvent(
    String eventId,
    String defectId,
    String aggregateId,
    Instant occurredAt,
    Map<String, Object> payload
) implements DomainEvent {
    public DefectReportedEvent(String defectId, Map<String, Object> payload) {
        this(UUID.randomUUID().toString(), defectId, "VForce360", Instant.now(), payload);
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
