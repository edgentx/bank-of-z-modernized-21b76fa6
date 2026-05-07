package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by the temporal worker to report defects.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message to a configured Slack channel.
     *
     * @param messageBody The formatted content of the message (Markdown). Contains the GitHub link.
     * @throws IllegalArgumentException if messageBody is null or blank.
     */
    void sendMessage(String messageBody);
}
