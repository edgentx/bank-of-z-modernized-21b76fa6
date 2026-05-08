package com.example.domain.validation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record DefectReportedEvent(
        String aggregateId,
        String description,
        String severity,
        String issueUrl,
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

    public String getIssueUrl() {
        return issueUrl();
    }
    public String getDescription() {
        return description();
    }
}