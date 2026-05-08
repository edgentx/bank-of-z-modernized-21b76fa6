package com.example.domain.vforce360.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;

public record DefectReportedEvent(
        String defectId,
        String title,
        String description,
        String severity,
        String githubIssueUrl,
        Map<String, String> metadata,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "DefectReported";
    }

    @Override
    public String aggregateId() {
        return defectId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}