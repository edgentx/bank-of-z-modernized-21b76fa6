package com.example.domain.validation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;

/**
 * Event representing that a defect has been reported and a GitHub issue + Slack notification
 * should have been generated.
 */
public record DefectReportedEvent(
        String defectId,
        String githubIssueUrl,
        String slackChannel,
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