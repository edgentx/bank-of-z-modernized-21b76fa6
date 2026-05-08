package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * This isolates the domain logic from the concrete Slack API implementation.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification about a defect to Slack.
     *
     * @param channel The Slack channel ID or name.
     * @param message The main message content.
     * @param githubIssueUrl The URL of the created GitHub issue (must be included in body).
     */
    void sendDefectNotification(String channel, String message, String githubIssueUrl);
}
