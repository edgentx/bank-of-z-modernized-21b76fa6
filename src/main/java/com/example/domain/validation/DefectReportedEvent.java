package com.example.domain.validation;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record DefectReportedEvent(
    String aggregateId,
    String defectId,
    String githubUrl,
    Instant occurredAt
) implements DomainEvent {
    @Override public String type() { return "DefectReported"; }
    @Override public String aggregateId() { return aggregateId; }
    @Override public Instant occurredAt() { return occurredAt; }
}
