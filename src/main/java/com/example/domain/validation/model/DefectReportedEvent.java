package com.example.domain.validation.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record DefectReportedEvent(
    String aggregateId,
    String defectId,
    String slackBody,
    String severity,
    Instant occurredAt
) implements DomainEvent {
    public DefectReportedEvent(String defectId, String slackBody, String severity) {
        this(UUID.randomUUID().toString(), defectId, slackBody, severity, Instant.now());
    }

    @Override
    public String type() {
        return "DefectReported";
    }

    @Override
    public String aggregateId() {
        return aggregateId();
    }

    @Override
    public Instant occurredAt() {
        return occurredAt();
    }
}
