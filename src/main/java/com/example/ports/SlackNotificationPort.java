package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * This decouples the domain logic from the specific Slack API implementation.
 */
public interface SlackNotificationPort {
    /**
     * Sends a notification to Slack.
     *
     * @param githubUrl The URL of the created GitHub issue to be included in the body.
     * @param title     The title of the defect/issue.
     */
    void sendNotification(String githubUrl, String title);
}
