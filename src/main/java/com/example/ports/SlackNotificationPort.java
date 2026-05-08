package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by the Defect Reporting workflow.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to a specific channel.
     *
     * @param channel The target Slack channel (e.g., "#vforce360-issues")
     * @param body    The formatted message body
     */
    void postMessage(String channel, String body);

    /**
     * Helper to validate a message before sending (implementation specific).
     * This method is expected to throw if validation fails.
     */
    void validateAndPost(String channel, String body);
}
