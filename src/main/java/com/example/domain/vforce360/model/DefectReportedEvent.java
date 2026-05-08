package com.example.domain.vforce360.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record DefectReportedEvent(
    String defectId,
    String githubIssueUrl,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "DefectReported";
    }

    @Override
    public String aggregateId() {
        return defectId();
    }

    @Override
    public Instant occurredAt() {
        return occurredAt();
    }
}
