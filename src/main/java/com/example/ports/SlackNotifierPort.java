package com.example.ports;

/**
 * Port interface for Slack notifications.
 * Adapters must implement this to send messages to a Slack channel.
 */
public interface SlackNotifierPort {

    /**
     * Sends a notification message to the configured Slack channel.
     *
     * @param message The main message text.
     * @param githubIssueUrl The URL of the GitHub issue to append/validate.
     */
    void sendNotification(String message, String githubIssueUrl);
}
