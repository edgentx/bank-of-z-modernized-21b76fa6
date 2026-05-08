package com.example.domain.validation.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Map;

/**
 * Event emitted when a defect report is generated.
 */
public record DefectReportedEvent(
    String aggregateId,
    String defectId,
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
}