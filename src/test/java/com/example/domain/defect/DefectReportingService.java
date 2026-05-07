package com.example.domain.defect;

import com.example.ports.SlackNotificationPort;

/**
 * Service responsible for reporting defects to external systems (e.g., Slack).
 * This acts as the Temporal Activity implementation or a domain service handler.
 */
public class DefectReportingService {

    private final SlackNotificationPort slackNotificationPort;

    /**
     * Constructor for dependency injection.
     *
     * @param slackNotificationPort The port for sending Slack notifications.
     */
    public DefectReportingService(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Reports a defect to Slack, ensuring the GitHub URL is included.
     * 
     * Acceptance Criteria:
     * - Slack body includes GitHub issue: <url>
     * - Validation fails if URL is missing
     *
     * @param defectId The ID of the defect (e.g., VW-454).
     * @param githubUrl The URL of the GitHub issue.
     * @throws IllegalArgumentException if defectId is null/blank.
     * @throws IllegalStateException if githubUrl is null/blank (validation failure).
     */
    public void reportDefect(String defectId, String githubUrl) {
        if (defectId == null || defectId.isBlank()) {
            throw new IllegalArgumentException("Defect ID cannot be null or empty");
        }

        // Requirement: The validation no longer exhibits the reported behavior (missing URL)
        // This is the fix for VW-454.
        if (githubUrl == null || githubUrl.isBlank()) {
            throw new IllegalStateException("Cannot report defect: GitHub URL is missing for " + defectId);
        }

        // Construct the message body ensuring the URL is present
        String messageBody = String.format(
            "Defect Reported: %s%nGitHub Issue: %s%nPlease investigate.",
            defectId,
            githubUrl
        );

        slackNotificationPort.sendNotification(messageBody);
    }
}
