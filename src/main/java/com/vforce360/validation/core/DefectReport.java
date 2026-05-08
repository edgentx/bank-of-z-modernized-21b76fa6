package com.vforce360.validation.core;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a persisted Defect Report.
 * Mapped to DB2/MongoDB via repository port.
 */
public class DefectReport {

    private final UUID id;
    private final String title;
    private final String description;
    private final Severity severity;
    private final String githubIssueUrl;
    private final LocalDateTime createdAt;

    public DefectReport(String title, String description, Severity severity, String githubIssueUrl) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.description = description;
        this.severity = severity;
        this.githubIssueUrl = githubIssueUrl;
        this.createdAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Severity getSeverity() {
        return severity;
    }

    public String getGithubIssueUrl() {
        return githubIssueUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
