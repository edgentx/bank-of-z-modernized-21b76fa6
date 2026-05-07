package com.example.domain.vforce360.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record DefectReportedEvent(
    String aggregateId,
    String title,
    String project,
    Instant occurredAt
) implements DomainEvent {

    public DefectReportedEvent {
        if (aggregateId == null || aggregateId.isBlank()) {
            aggregateId = UUID.randomUUID().toString();
        }
    }

    @Override
    public String type() {
        return "DefectReported";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}
