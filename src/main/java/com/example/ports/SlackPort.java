package com.example.ports;

/**
 * Port interface for Slack notification operations.
 * This allows us to mock the Slack API in tests.
 */
public interface SlackPort {

    /**
     * Sends an alert message to a Slack channel.
     *
     * @param channel The channel ID or name (e.g., "#alerts").
     * @param message The core message content.
     * @param githubIssueUrl The URL to the related GitHub issue (can be null/blank).
     */
    void sendAlert(String channel, String message, String githubIssueUrl);
}
