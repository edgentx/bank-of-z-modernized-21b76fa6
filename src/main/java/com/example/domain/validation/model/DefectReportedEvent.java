package com.example.domain.validation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

public class DefectReportedEvent implements DomainEvent {
    private final String defectId;
    private final String projectId;
    private final String title;
    private final String severity;
    private final String githubUrl;
    private final String slackBody;
    private final Instant occurredAt;

    public DefectReportedEvent(String defectId, String projectId, String title, String severity, String githubUrl, String slackBody, Instant occurredAt) {
        this.defectId = defectId;
        this.projectId = projectId;
        this.title = title;
        this.severity = severity;
        this.githubUrl = githubUrl;
        this.slackBody = slackBody;
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

    public String defectId() { return defectId; }
    public String projectId() { return projectId; }
    public String title() { return title; }
    public String severity() { return severity; }
    public String githubUrl() { return githubUrl; }
    public String slackBody() { return slackBody; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefectReportedEvent that = (DefectReportedEvent) o;
        return Objects.equals(defectId, that.defectId) && Objects.equals(githubUrl, that.githubUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(defectId, githubUrl);
    }
}
