package com.example.domain.defect.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

/**
 * Event representing the internal logging of a defect report.
 * This is the domain event expected to be raised by the DefectAggregate
 * when a valid defect is reported.
 */
public record VW454DefectReportedEvent(
    String aggregateId,
    String defectId,
    String title,
    String severity,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "VW454DefectReported";
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
