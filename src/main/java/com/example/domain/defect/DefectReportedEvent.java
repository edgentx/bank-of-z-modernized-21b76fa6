package com.example.domain.defect;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a defect report is generated.
 */
public record DefectReportedEvent(
        String aggregateId,
        String defectId,
        String targetChannel,
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
