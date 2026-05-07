package com.example.domain.validation.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record DefectReportedEvent(
    String aggregateId,
    String title,
    String githubIssueUrl,
    Instant occurredAt
) implements DomainEvent {
    @Override public String type() { return "DefectReported"; }
    @Override public String aggregateId() { return aggregateId(); }
    @Override public Instant occurredAt() { return occurredAt; }
}
