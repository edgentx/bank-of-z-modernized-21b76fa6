package com.example.domain.vforce;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event representing a defect reported in the VForce360 system.
 */
public class DefectReportedEvent implements DomainEvent {

    private final String eventId = UUID.randomUUID().toString();
    private final String defectId;
    private final String title;
    private final String githubUrl;
    private final Instant occurredAt;

    public DefectReportedEvent(String defectId, String title, String githubUrl, Instant occurredAt) {
        this.defectId = defectId;
        this.title = title;
        this.githubUrl = githubUrl;
        this.occurredAt = occurredAt;
    }

    @Override
    public String type() {
        return "DefectReported";
    }

    @Override
    public String aggregateId() {
        return defectId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    public String getDefectId() {
        return defectId;
    }

    public String getTitle() {
        return title;
    }

    public String getGithubUrl() {
        return githubUrl;
    }
}
