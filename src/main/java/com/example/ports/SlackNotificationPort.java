package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used to verify GitHub issue links are included in defect reports.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message payload to a configured Slack channel.
     *
     * @param messageBody The JSON string payload to be sent.
     * @throws IllegalArgumentException if the messageBody is invalid.
     * @throws RuntimeException if the external API call fails.
     */
    void sendMessage(String messageBody);
}