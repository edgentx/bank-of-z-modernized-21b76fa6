package com.example.domain.reporting.model;
import com.example.domain.shared.DomainEvent;
import java.time.Instant;
public record DefectReportedEvent(
    String defectId,
    String title,
    String severity,
    String component,
    String githubIssueUrl,
    String slackBody,
    Instant occurredAt
) implements DomainEvent {
  @Override public String type() { return "defect.reported"; }
  @Override public String aggregateId() { return defectId; }
}
