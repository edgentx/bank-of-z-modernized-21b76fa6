package com.example.domain.defect;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Map;

/**
 * Event emitted when a defect is reported. Migrated to POJO to support Java 11.
 */
public class DefectReportedEvent implements DomainEvent {
    private final String aggregateId;
    private final String type = "DefectReported";
    private final Instant occurredAt;
    private final String githubUrl;

    public DefectReportedEvent(String aggregateId, String githubUrl, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.githubUrl = githubUrl;
        this.occurredAt = occurredAt;
    }

    @Override
    public String type() { return type; }
    @Override
    public String aggregateId() { return aggregateId; }
    @Override
    public Instant occurredAt() { return occurredAt; }
    public String getGithubUrl() { return githubUrl; }
}
