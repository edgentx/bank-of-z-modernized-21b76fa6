package com.example.domain.vforce360;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record DefectReportedEvent(
    String defectId,
    String gitHubIssueUrl,
    String slackChannelId,
    Instant occurredAt
) implements DomainEvent {
    @Override public String type() { return "DefectReported"; }
    @Override public String aggregateId() { return defectId; }
    @Override public Instant occurredAt() { return occurredAt; }
}