package com.example.domain.vforce360.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record ReportDefectTriggeredEvent(
    String projectId,
    String validationId,
    String defectId,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "ReportDefectTriggered";
    }

    @Override
    public String aggregateId() {
        return projectId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}