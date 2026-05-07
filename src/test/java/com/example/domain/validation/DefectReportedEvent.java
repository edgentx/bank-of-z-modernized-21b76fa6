package com.example.domain.validation;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Map;

public record DefectReportedEvent(
    String defectId,
    String title,
    String severity,
    String component,
    String projectId,
    String slackBody,
    String githubUrl,
    Map<String, String> context,
    Instant occurredAt
) implements DomainEvent {
    @Override public String type() { return "DefectReported"; }
    @Override public String aggregateId() { return defectId; }
    @Override public Instant occurredAt() { return occurredAt; }
}
