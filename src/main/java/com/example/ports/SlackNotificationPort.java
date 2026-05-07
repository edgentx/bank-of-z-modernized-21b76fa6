package com.example.ports;

/**
 * Port interface for sending notifications to Slack.
 * This decouples the business logic from the specific Slack client implementation.
 */
public interface SlackNotificationPort {

    /**
     * Sends a defect notification to the configured Slack channel.
     *
     * @param defectId The unique identifier of the defect (e.g., "VW-454").
     * @param description A human-readable description of the defect.
     * @param githubIssueUrl The direct URL to the GitHub issue tracking this defect.
     */
    void sendDefectNotification(String defectId, String description, String githubIssueUrl);
}
