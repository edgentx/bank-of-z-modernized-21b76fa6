package com.example.domain.reporting.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

public record DefectReportedEvent(
    String aggregateId,
    String defectId,
    String githubUrl,
    String slackMessageBody,
    Instant occurredAt
) implements DomainEvent {
  @Override public String type() { return "DefectReported"; }
}
