package com.example.domain.reconciliation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Event emitted when a defect is reported via VForce360.
 * Story S-FB-1: Validating GitHub URL presence.
 */
public class DefectReportedEvent implements DomainEvent {
    private final String defectId;
    private final String aggregateId;
    private final Instant occurredAt;
    private final String slackBody;
    private final String githubUrl;

    public DefectReportedEvent(String defectId, String aggregateId, String slackBody, String githubUrl) {
        this.defectId = defectId;
        this.aggregateId = aggregateId;
        this.occurredAt = Instant.now();
        this.slackBody = slackBody;
        this.githubUrl = githubUrl;
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

    public String defectId() { return defectId; }
    public String slackBody() { return slackBody; }
    public String githubUrl() { return githubUrl; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefectReportedEvent that = (DefectReportedEvent) o;
        return Objects.equals(defectId, that.defectId) && Objects.equals(aggregateId, that.aggregateId) && Objects.equals(slackBody, that.slackBody) && Objects.equals(githubUrl, that.githubUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(defectId, aggregateId, slackBody, githubUrl);
    }
}
