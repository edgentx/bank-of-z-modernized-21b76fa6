package com.example.domain.validation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record DefectReportedEvent(
    String reportId,
    String githubUrl,
    String slackBody,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "DefectReported";
    }

    @Override
    public String aggregateId() {
        return reportId();
    }

    @Override
    public Instant occurredAt() {
        return occurredAt();
    }
}