package com.example.domain.vforce360.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record VForce360DefectReportedEvent(
    String aggregateId,
    String defectId,
    String status,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "VForce360DefectReported";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}
