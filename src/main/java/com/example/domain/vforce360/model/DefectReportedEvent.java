package com.example.domain.vforce360.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

public record DefectReportedEvent(
    String aggregateId,
    String title,
    String severity,
    String component,
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
