package com.example.domain.vforce360;

/**
 * Domain event indicating a defect has been reported and tracked in Slack/GitHub.
 */
public record DefectReportedException(String issueId, String githubUrl) {}
