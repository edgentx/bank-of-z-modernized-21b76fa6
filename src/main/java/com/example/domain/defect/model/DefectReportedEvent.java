package com.example.domain.defect.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Objects;

/**
 * Event emitted when a defect is reported in the VForce360 system.
 * Corresponds to the VW-454 defect scenario regarding Slack notifications.
 */
public class DefectReportedEvent implements DomainEvent {
    private final String aggregateId;
    private final String issueId;
    private final String description;
    private final Instant occurredAt;

    public DefectReportedEvent(String aggregateId, String issueId, String description, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.issueId = issueId;
        this.description = description;
        this.occurredAt = occurredAt;
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

    public String getIssueId() {
        return issueId;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefectReportedEvent that = (DefectReportedEvent) o;
        return Objects.equals(aggregateId, that.aggregateId) &&
                Objects.equals(issueId, that.issueId) &&
                Objects.equals(description, that.description) &&
                Objects.equals(occurredAt, that.occurredAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateId, issueId, description, occurredAt);
    }
}
