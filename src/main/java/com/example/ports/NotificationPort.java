package com.example.ports;

/**
 * Port interface for sending notifications to external systems like Slack or GitHub.
 * Implementations are responsible for the actual HTTP integrations.
 */
public interface NotificationPort {

    /**
     * Posts a message to a Slack channel.
     *
     * @param channel The target channel (e.g. #vforce360-issues)
     * @param message The message body content
     * @return true if the message was accepted by the system, false otherwise
     */
    boolean postToSlack(String channel, String message);

    /**
     * Creates a GitHub issue.
     *
     * @param title The issue title
     * @param body The issue body content
     * @return The URL of the created issue
     */
    String createGitHubIssue(String title, String body);
}
