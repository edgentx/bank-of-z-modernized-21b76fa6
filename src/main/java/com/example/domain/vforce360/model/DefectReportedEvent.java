package com.example.domain.vforce360.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record DefectReportedEvent(
        String aggregateId,
        String defectId,
        String title,
        String description,
        String githubIssueUrl,
        Instant occurredAt
) implements DomainEvent {
    public DefectReportedEvent {
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId required");
    }

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

    public static DefectReportedEvent create(String title, String description, String githubIssueUrl) {
        String aggregateId = UUID.randomUUID().toString();
        return new DefectReportedEvent(
                aggregateId,
                "DEF-" + aggregateId.substring(0, 8),
                title,
                description,
                githubIssueUrl,
                Instant.now()
        );
    }
}