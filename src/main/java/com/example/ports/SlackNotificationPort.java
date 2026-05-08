package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by the Temporal workflow logic.
 */
public interface SlackNotificationPort {

    /**
     * Sends a defect report to the configured Slack channel.
     *
     * @param defectId The ID of the defect being reported.
     * @param message The body of the message.
     * @param gitHubIssueUrl The URL to the GitHub issue (must be included in body).
     */
    void sendDefectReport(String defectId, String message, String gitHubIssueUrl);
}
