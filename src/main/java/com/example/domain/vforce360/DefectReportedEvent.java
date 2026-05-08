package com.example.domain.vforce360;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record DefectReportedEvent(String projectId, String issueUrl, Instant occurredAt) implements DomainEvent {
    @Override
    public String type() {
        return "DefectReported";
    }

    @Override
    public String aggregateId() {
        return projectId;
    }
}
