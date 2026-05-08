package com.example.domain.validation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record DefectReportedEvent(
        String aggregateId,
        String defectId,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "DefectReportedEvent";
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
