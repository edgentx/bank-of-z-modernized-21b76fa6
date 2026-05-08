package com.example.domain.validation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;

/**
 * Event emitted when a defect is successfully reported.
 * Part of Story S-FB-1: Validating VW-454.
 */
public record DefectReportedEvent(
        String aggregateId,
        String defectId,
        String issueUrl,
        Instant occurredAt,
        Map<String, String> metadata
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
