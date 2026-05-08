package com.example.domain.defect.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;

public record DefectReportedEvent(String aggregateId, String projectId, String title, String description, String githubIssueUrl, Map<String, String> metadata, Instant occurredAt) implements DomainEvent {
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